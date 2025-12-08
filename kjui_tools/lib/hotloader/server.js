#!/usr/bin/env node

const express = require('express');
const http = require('http');
const WebSocket = require('ws');
const chokidar = require('chokidar');
const fs = require('fs');
const path = require('path');
const { exec } = require('child_process');

// Configuration
const PORT = process.env.PORT || 8081;
const HOST = process.env.HOST || '0.0.0.0';

// Create Express app
const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

// Find project root by looking for kjui.config.json
function findProjectRoot(startPath = process.cwd()) {
    let currentPath = startPath;
    
    // First, check if kjui.config.json exists in current or parent directories
    while (currentPath !== '/') {
        if (fs.existsSync(path.join(currentPath, 'kjui.config.json'))) {
            return currentPath;
        }
        
        // Check subdirectories for kjui.config.json
        try {
            const dirs = fs.readdirSync(currentPath, { withFileTypes: true })
                .filter(dirent => dirent.isDirectory())
                .map(dirent => dirent.name);
            
            for (const dir of dirs) {
                const configPath = path.join(currentPath, dir, 'kjui.config.json');
                if (fs.existsSync(configPath)) {
                    return path.join(currentPath, dir);
                }
            }
        } catch (e) {
            // Ignore permission errors
        }
        
        currentPath = path.dirname(currentPath);
    }
    
    return process.cwd();
}

// Load configuration
// Use PROJECT_ROOT env var if provided (from hotload.rb), otherwise search for it
const projectRoot = process.env.PROJECT_ROOT || findProjectRoot();
const configPath = path.join(projectRoot, 'kjui.config.json');
let config = {};

if (fs.existsSync(configPath)) {
    try {
        config = JSON.parse(fs.readFileSync(configPath, 'utf8'));
    } catch (e) {
        console.error('Error loading kjui.config.json:', e);
    }
}

// Use paths from config or defaults
const layoutsDir = path.join(projectRoot, config.source_directory || 'src/main', config.layouts_directory || 'assets/Layouts');
const stylesDir = path.join(projectRoot, config.source_directory || 'src/main', config.styles_directory || 'assets/Styles');

console.log('KotlinJsonUI HotLoader Server');
console.log('=============================');
console.log(`Project root: ${projectRoot}`);
console.log(`Layouts directory: ${layoutsDir}`);
console.log(`Styles directory: ${stylesDir}`);
console.log(`Server: http://${HOST}:${PORT}`);
console.log('');

// Middleware
app.use(express.json());
app.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
    next();
});

// Routes
app.get('/', (req, res) => {
    res.json({
        status: 'running',
        version: '1.0.0',
        project: 'KotlinJsonUI',
        projectRoot: projectRoot,
        connectedClients: wss.clients.size
    });
});

// Get layout file
app.get('/layout/:name', (req, res) => {
    const layoutName = req.params.name;
    const layoutPath = path.join(layoutsDir, `${layoutName}.json`);
    
    if (fs.existsSync(layoutPath)) {
        const content = fs.readFileSync(layoutPath, 'utf8');
        res.json(JSON.parse(content));
    } else {
        res.status(404).json({ error: 'Layout not found' });
    }
});

// Get style file
app.get('/style/:name', (req, res) => {
    const styleName = req.params.name;
    const stylePath = path.join(stylesDir, `${styleName}.json`);
    
    if (fs.existsSync(stylePath)) {
        const content = fs.readFileSync(stylePath, 'utf8');
        res.json(JSON.parse(content));
    } else {
        res.status(404).json({ error: 'Style not found' });
    }
});

// List all layouts
app.get('/layouts', (req, res) => {
    if (fs.existsSync(layoutsDir)) {
        const files = fs.readdirSync(layoutsDir)
            .filter(file => file.endsWith('.json'))
            .map(file => file.replace('.json', ''));
        res.json(files);
    } else {
        res.json([]);
    }
});

// WebSocket connection handling
wss.on('connection', (ws, req) => {
    const clientIp = req.socket.remoteAddress;
    console.log(`Client connected from ${clientIp}`);
    
    // Send initial connection confirmation
    ws.send(JSON.stringify({
        type: 'connected',
        message: 'Connected to KotlinJsonUI HotLoader'
    }));
    
    ws.on('close', () => {
        console.log(`Client disconnected from ${clientIp}`);
    });
    
    ws.on('error', (error) => {
        console.error(`WebSocket error from ${clientIp}:`, error);
    });
});

// Broadcast message to all connected clients
function broadcast(message) {
    wss.clients.forEach((client) => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(JSON.stringify(message));
        }
    });
}

// File watcher
const watchDirs = [];
if (fs.existsSync(layoutsDir)) watchDirs.push(layoutsDir);
if (fs.existsSync(stylesDir)) watchDirs.push(stylesDir);

if (watchDirs.length > 0) {
    const watcher = chokidar.watch(watchDirs, {
        ignored: [
            /(^|[\/\\])\../, // ignore dotfiles
            /Resources/, // ignore Resources folders to prevent infinite loop
            /node_modules/,
            /build/,
            /\.gradle/
        ],
        persistent: true,
        ignoreInitial: true
    });
    
    // Run kjui build when files change
    function runBuild() {
        console.log('Running kjui build...');
        // Find kjui relative to this server.js file
        // server.js is at kjui_tools/lib/hotloader/server.js
        // kjui is at kjui_tools/bin/kjui
        const serverDir = __dirname; // kjui_tools/lib/hotloader
        const kjuiPath = path.join(serverDir, '..', '..', 'bin', 'kjui');
        const absoluteKjuiPath = path.resolve(kjuiPath);
        
        if (!fs.existsSync(absoluteKjuiPath)) {
            console.error(`kjui not found at ${absoluteKjuiPath}`);
            return;
        }
        
        exec(`ruby ${absoluteKjuiPath} build`, { cwd: projectRoot }, (error, stdout, stderr) => {
            if (error) {
                console.error('Build error:', error);
                return;
            }
            if (stderr) {
                console.error('Build stderr:', stderr);
            }
            if (stdout && stdout.trim()) {
                console.log('Build output:', stdout);
            }
            console.log('Build completed successfully');
        });
    }
    
    watcher
        .on('add', (filepath) => {
            // Skip Resources folder files
            if (filepath.includes('Resources')) {
                console.log(`Ignoring Resources file: ${filepath}`);
                return;
            }
            
            console.log(`File added: ${filepath}`);
            const relativePath = path.relative(projectRoot, filepath);
            const dirName = path.basename(path.dirname(filepath));
            const fileName = path.basename(filepath, '.json');
            
            broadcast({
                type: 'file_added',
                path: relativePath,
                dirName: dirName,
                fileName: fileName
            });
            
            if (filepath.includes('Layouts')) {
                runBuild();
            }
        })
        .on('change', (filepath) => {
            // Skip Resources folder files
            if (filepath.includes('Resources')) {
                console.log(`Ignoring Resources file change: ${filepath}`);
                return;
            }
            
            console.log(`File changed: ${filepath}`);
            const relativePath = path.relative(projectRoot, filepath);
            const dirName = path.basename(path.dirname(filepath));
            const fileName = path.basename(filepath, '.json');
            
            broadcast({
                type: 'file_changed',
                path: relativePath,
                dirName: dirName,
                fileName: fileName
            });
            
            if (filepath.includes('Layouts')) {
                runBuild();
            }
        })
        .on('unlink', (filepath) => {
            // Skip Resources folder files
            if (filepath.includes('Resources')) {
                console.log(`Ignoring Resources file removal: ${filepath}`);
                return;
            }
            
            console.log(`File removed: ${filepath}`);
            const relativePath = path.relative(projectRoot, filepath);
            const dirName = path.basename(path.dirname(filepath));
            const fileName = path.basename(filepath, '.json');
            
            broadcast({
                type: 'file_removed',
                path: relativePath,
                dirName: dirName,
                fileName: fileName
            });
            
            if (filepath.includes('Layouts')) {
                runBuild();
            }
        })
        .on('error', (error) => {
            console.error('Watcher error:', error);
        });
    
    console.log('Watching for file changes...');
} else {
    console.warn('No directories to watch. Please ensure Layouts and/or Styles directories exist.');
}

// Start server
server.listen(PORT, HOST, () => {
    console.log(`\nServer running at http://${HOST}:${PORT}`);
    console.log('WebSocket endpoint: ws://' + HOST + ':' + PORT);
    console.log('\nPress Ctrl+C to stop the server');
});

// Graceful shutdown
process.on('SIGINT', () => {
    console.log('\nShutting down server...');
    wss.clients.forEach((client) => {
        client.close();
    });
    server.close(() => {
        console.log('Server stopped');
        process.exit(0);
    });
});