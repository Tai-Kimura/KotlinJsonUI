# KotlinJsonUI Installer

This directory contains installation scripts for `kjui_tools`, the code generation and hot reload tools for KotlinJsonUI.

## Quick Installation

### One-line Installation (Recommended)

Run this command from your Android project directory:

```bash
curl -fsSL https://raw.githubusercontent.com/Tai-Kimura/KotlinJsonUI/main/installer/bootstrap.sh | bash
```

This will:
- Check and set up Ruby environment
- Download and install `kjui_tools`
- Install Ruby dependencies
- Install Node.js dependencies for HotLoader
- Create initial configuration

### Installation Options

#### Install specific version
```bash
curl -fsSL https://raw.githubusercontent.com/Tai-Kimura/KotlinJsonUI/main/installer/bootstrap.sh | bash -s -- -v v1.0.0
```

#### Install for XML View mode
```bash
curl -fsSL https://raw.githubusercontent.com/Tai-Kimura/KotlinJsonUI/main/installer/bootstrap.sh | bash -s -- -m xml
```

#### Install in specific directory
```bash
curl -fsSL https://raw.githubusercontent.com/Tai-Kimura/KotlinJsonUI/main/installer/bootstrap.sh | bash -s -- -d ./my-project
```

#### Skip bundle install
```bash
curl -fsSL https://raw.githubusercontent.com/Tai-Kimura/KotlinJsonUI/main/installer/bootstrap.sh | bash -s -- -s
```

## Local Installation

If you've cloned the repository, you can run the installer locally:

```bash
# From your Android project directory
/path/to/KotlinJsonUI/installer/install_kjui.sh

# With options
/path/to/KotlinJsonUI/installer/install_kjui.sh -v main -m compose
```

## Installation Modes

### Compose Mode (Default)
- Generates Jetpack Compose code
- Creates ViewModels with state management
- Supports dynamic mode for hot reload
- Modern Android UI toolkit

### XML Mode
- Generates traditional Android XML layouts
- Creates ViewBinding code
- Compatible with older Android projects
- Legacy support

## Requirements

### Required
- **Ruby**: 2.7.0 or later (3.2.2 recommended)
- **Android Studio**: Latest stable version
- **Gradle**: 7.0 or later

### Recommended
- **Node.js**: 14+ (for HotLoader functionality)
- **npm**: For installing HotLoader dependencies
- **rbenv or rvm**: For Ruby version management

## Post-Installation

After installation, you'll have `kjui_tools` in your project. The typical workflow is:

### 1. Initialize Configuration
```bash
kjui_tools/bin/kjui init
```

### 2. Set Up Project
```bash
kjui_tools/bin/kjui setup
```

### 3. Generate Code
```bash
# Generate code for all JSON layouts
kjui_tools/bin/kjui generate

# Generate code for specific layout
kjui_tools/bin/kjui generate --layout test_menu
```

### 4. Enable Hot Reload (Compose mode only)
```bash
kjui_tools/bin/kjui watch
```

## File Structure

After installation, you'll have:

```
your-android-project/
├── kjui_tools/
│   ├── bin/
│   │   ├── kjui           # Main CLI executable
│   │   └── install_deps    # Dependency installer
│   ├── lib/
│   │   ├── cli/           # CLI commands
│   │   ├── compose/       # Compose generators
│   │   ├── xml/           # XML generators
│   │   └── hotloader/     # Hot reload server
│   ├── config/
│   ├── Gemfile
│   ├── Gemfile.lock
│   ├── VERSION
│   └── MODE
├── app/
│   └── src/main/
│       └── assets/
│           ├── Layouts/   # JSON layout files
│           └── Styles/    # JSON style files
└── ...
```

## Commands

### kjui init
Initialize KotlinJsonUI configuration for your project.

### kjui generate (g)
Generate Kotlin code from JSON layouts.

Options:
- `--layout <name>`: Generate specific layout
- `--force`: Force regeneration

### kjui build (b)
Build all layouts and update dependencies.

### kjui watch (w)
Start hot reload server (Compose mode only).

### kjui cell
Generate cell layouts for collections.

### kjui converter
Generate custom component converters.

### kjui view
Create new view from JSON.

### kjui help
Show all available commands.

## Troubleshooting

### Ruby Issues

If you encounter Ruby version issues:

1. Install rbenv:
```bash
# macOS
brew install rbenv
rbenv init

# Linux
git clone https://github.com/rbenv/rbenv.git ~/.rbenv
echo 'export PATH="$HOME/.rbenv/bin:$PATH"' >> ~/.bashrc
echo 'eval "$(rbenv init -)"' >> ~/.bashrc
```

2. Install Ruby:
```bash
rbenv install 3.2.2
rbenv global 3.2.2
```

### Node.js Issues

For HotLoader functionality, install Node.js:

```bash
# macOS
brew install node

# Or use nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 18
nvm use 18
```

### Permission Issues

If you get permission errors:

```bash
chmod +x kjui_tools/bin/kjui
chmod +x kjui_tools/bin/install_deps
```

### Bundle Install Fails

If bundle install fails:

```bash
cd kjui_tools
gem install bundler
bundle install
```

## Manual Installation

If the automated installer doesn't work, you can install manually:

1. Download the repository:
```bash
git clone https://github.com/Tai-Kimura/KotlinJsonUI.git
```

2. Copy kjui_tools to your project:
```bash
cp -r KotlinJsonUI/kjui_tools /path/to/your/project/
```

3. Make scripts executable:
```bash
chmod +x kjui_tools/bin/kjui
chmod +x kjui_tools/bin/install_deps
```

4. Install dependencies:
```bash
cd kjui_tools
bundle install
cd lib/hotloader && npm install
```

## Support

For issues or questions:
- GitHub Issues: https://github.com/Tai-Kimura/KotlinJsonUI/issues
- Documentation: https://github.com/Tai-Kimura/KotlinJsonUI/wiki