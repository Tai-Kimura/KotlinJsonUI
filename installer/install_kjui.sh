#!/usr/bin/env bash

# KotlinJsonUI Installer Script
# This script downloads and installs kjui_tools (unified tool for XML and Compose generation)

set -e

# Default values
GITHUB_REPO="Tai-Kimura/KotlinJsonUI"
DEFAULT_BRANCH="main"
INSTALL_DIR=".."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Function to show usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -v, --version <version>    Specify version/branch/tag/commit to download (default: main)"
    echo "  -d, --directory <dir>      Installation directory (default: parent directory)"
    echo "  -m, --mode <mode>          Installation mode: xml or compose (default: compose)"
    echo "  -s, --skip-bundle          Skip bundle install for Ruby dependencies"
    echo "  -h, --help                 Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                         # Install latest from main branch to parent directory"
    echo "  $0 -v v1.0.0               # Install specific version (tag)"
    echo "  $0 -v 1.2.0                # Install from branch (e.g., unreleased version)"
    echo "  $0 -v feature-branch       # Install from specific branch"
    echo "  $0 -v a1b2c3d              # Install from specific commit hash"
    echo "  $0 -d ./my-project         # Install in specific directory"
    echo "  $0 -m xml                  # Install for XML View mode"
    echo "  $0 -s                      # Skip bundle install"
    exit 0
}

# Parse command line arguments
VERSION=""
SKIP_BUNDLE=false
MODE="compose"

while [[ $# -gt 0 ]]; do
    case $1 in
        -v|--version)
            VERSION="$2"
            shift 2
            ;;
        -d|--directory)
            INSTALL_DIR="$2"
            shift 2
            ;;
        -m|--mode)
            MODE="$2"
            if [[ "$MODE" != "xml" && "$MODE" != "compose" ]]; then
                print_error "Invalid mode: $MODE. Must be 'xml' or 'compose'"
                usage
            fi
            shift 2
            ;;
        -s|--skip-bundle)
            SKIP_BUNDLE=true
            shift
            ;;
        -h|--help)
            usage
            ;;
        *)
            print_error "Unknown option: $1"
            usage
            ;;
    esac
done

# Use default branch if no version specified
if [ -z "$VERSION" ]; then
    VERSION="$DEFAULT_BRANCH"
fi

# Validate installation directory
if [ ! -d "$INSTALL_DIR" ]; then
    print_error "Installation directory does not exist: $INSTALL_DIR"
    exit 1
fi

# Change to installation directory
cd "$INSTALL_DIR"

print_info "Installing KotlinJsonUI tools..."
print_info "Version: $VERSION"
print_info "Mode: $MODE"
print_info "Directory: $(pwd)"

# Check if kjui_tools already exists
if [ -d "kjui_tools" ]; then
    print_warning "kjui_tools directory already exists."
    read -p "Do you want to overwrite it? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Installation cancelled."
        exit 0
    else
        rm -rf kjui_tools
    fi
fi

# Create temporary directory for download
TEMP_DIR=$(mktemp -d)
print_info "Created temporary directory: $TEMP_DIR"

# Cleanup function
cleanup() {
    if [ -d "$TEMP_DIR" ]; then
        print_info "Cleaning up temporary files..."
        rm -rf "$TEMP_DIR"
    fi
}

# Set trap to cleanup on exit
trap cleanup EXIT

# Download the archive
print_info "Downloading KotlinJsonUI $VERSION..."

# Determine download URL based on VERSION format:
# - v1.0.0 or 1.0.0 (with dots) → tag
# - a1b2c3d (7-40 hex chars, no dots) → commit hash
# - anything else → branch
if [[ "$VERSION" =~ ^v[0-9]+\.[0-9]+ ]]; then
    # Tag with 'v' prefix (e.g., v1.0.0)
    DOWNLOAD_URL="https://github.com/$GITHUB_REPO/archive/refs/tags/$VERSION.tar.gz"
    print_info "Detected: tag"
elif [[ "$VERSION" =~ ^[0-9]+\.[0-9]+ ]] && [[ ! "$VERSION" =~ ^[0-9a-fA-F]+$ ]]; then
    # Version number without 'v' but with dots (e.g., 1.2.0) - treat as branch
    DOWNLOAD_URL="https://github.com/$GITHUB_REPO/archive/$VERSION.tar.gz"
    print_info "Detected: branch (version number)"
elif [[ "$VERSION" =~ ^[0-9a-fA-F]{7,40}$ ]]; then
    # Commit hash (7-40 hex characters)
    DOWNLOAD_URL="https://github.com/$GITHUB_REPO/archive/$VERSION.tar.gz"
    print_info "Detected: commit hash"
else
    # Branch name
    DOWNLOAD_URL="https://github.com/$GITHUB_REPO/archive/$VERSION.tar.gz"
    print_info "Detected: branch"
fi

if ! curl -L -f -o "$TEMP_DIR/kotlinjsonui.tar.gz" "$DOWNLOAD_URL"; then
    print_error "Failed to download from $DOWNLOAD_URL"
    print_error "Please check if the version/branch '$VERSION' exists."
    exit 1
fi

# Extract the archive
print_info "Extracting archive..."
tar -xzf "$TEMP_DIR/kotlinjsonui.tar.gz" -C "$TEMP_DIR"

# Find the extracted directory (it will have a dynamic name based on version)
EXTRACT_DIR=$(find "$TEMP_DIR" -maxdepth 1 -type d -name "KotlinJsonUI-*" | head -1)

if [ -z "$EXTRACT_DIR" ]; then
    print_error "Failed to find extracted directory"
    exit 1
fi

# Copy kjui_tools
if [ -d "$EXTRACT_DIR/kjui_tools" ]; then
    print_info "Installing kjui_tools..."
    cp -r "$EXTRACT_DIR/kjui_tools" .
    
    # Create VERSION file with the downloaded version
    echo "$VERSION" > kjui_tools/VERSION
    print_info "Set kjui_tools version to: $VERSION"
    
    # Create MODE file to track installation mode
    echo "$MODE" > kjui_tools/MODE
    print_info "Set installation mode to: $MODE"
    
    # Make kjui executable
    if [ -f "kjui_tools/bin/kjui" ]; then
        chmod +x kjui_tools/bin/kjui
        print_info "Made kjui_tools/bin/kjui executable"
    fi
    
    # Make install_deps executable
    if [ -f "kjui_tools/bin/install_deps" ]; then
        chmod +x kjui_tools/bin/install_deps
        print_info "Made kjui_tools/bin/install_deps executable"
    fi
    
    # Make all .sh and .rb files executable
    find kjui_tools -name "*.sh" -type f -exec chmod +x {} \;
    find kjui_tools -name "*.rb" -type f -exec chmod +x {} \;
    
    print_info "✅ kjui_tools installed successfully"
else
    print_error "kjui_tools not found in the downloaded version"
    exit 1
fi

# Install HotLoader Node.js dependencies
if [ -d "kjui_tools/lib/hotloader" ] && [ -f "kjui_tools/lib/hotloader/package.json" ]; then
    HOT_LOADER_DIR="kjui_tools/lib/hotloader"
    print_info "Installing HotLoader Node.js dependencies..."
    cd "$HOT_LOADER_DIR"
    if command -v npm &> /dev/null; then
        if npm install; then
            cd - > /dev/null
            print_info "✅ HotLoader Node.js dependencies installed"
        else
            cd - > /dev/null
            print_warning "Failed to install HotLoader Node.js dependencies"
            print_warning "You can install them manually later:"
            print_warning "  cd $HOT_LOADER_DIR && npm install"
        fi
    else
        cd - > /dev/null
        print_warning "npm not found. Please install Node.js and npm"
        print_warning "Then run: cd $HOT_LOADER_DIR && npm install"
    fi
fi

# Install Ruby dependencies
if [ -f "kjui_tools/Gemfile" ] && [ "$SKIP_BUNDLE" != true ]; then
    GEMFILE_DIR="kjui_tools"
    print_info "Installing Ruby dependencies..."
    
    # Check and setup Ruby version
    REQUIRED_RUBY_VERSION="3.2.2"
    MINIMUM_RUBY_VERSION="2.7.0"
    
    # Function to compare version numbers
    version_compare() {
        printf '%s\n%s' "$1" "$2" | sort -V | head -n1
    }
    
    # Check if rbenv is installed
    if command -v rbenv &> /dev/null; then
        print_info "Found rbenv"
        CURRENT_RUBY_VERSION=$(ruby -v | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1)
        
        if [ "$(version_compare "$CURRENT_RUBY_VERSION" "$MINIMUM_RUBY_VERSION")" != "$MINIMUM_RUBY_VERSION" ]; then
            print_info "Current Ruby version ($CURRENT_RUBY_VERSION) is too old"
            print_info "Installing Ruby $REQUIRED_RUBY_VERSION with rbenv..."
            
            # Install required Ruby version
            if rbenv install -s "$REQUIRED_RUBY_VERSION"; then
                rbenv local "$REQUIRED_RUBY_VERSION"
                print_info "Ruby $REQUIRED_RUBY_VERSION installed and set as local version"
            else
                print_warning "Failed to install Ruby $REQUIRED_RUBY_VERSION"
                print_warning "Please install it manually: rbenv install $REQUIRED_RUBY_VERSION"
            fi
        else
            print_info "Ruby version $CURRENT_RUBY_VERSION is compatible"
        fi
    # Check if rvm is installed
    elif command -v rvm &> /dev/null || [ -s "$HOME/.rvm/scripts/rvm" ]; then
        print_info "Found rvm"
        # Source rvm if needed
        [ -s "$HOME/.rvm/scripts/rvm" ] && source "$HOME/.rvm/scripts/rvm"
        
        CURRENT_RUBY_VERSION=$(ruby -v | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1)
        
        if [ "$(version_compare "$CURRENT_RUBY_VERSION" "$MINIMUM_RUBY_VERSION")" != "$MINIMUM_RUBY_VERSION" ]; then
            print_info "Current Ruby version ($CURRENT_RUBY_VERSION) is too old"
            print_info "Installing Ruby $REQUIRED_RUBY_VERSION with rvm..."
            
            # Install required Ruby version
            if rvm install "$REQUIRED_RUBY_VERSION"; then
                rvm use "$REQUIRED_RUBY_VERSION"
                print_info "Ruby $REQUIRED_RUBY_VERSION installed and activated"
            else
                print_warning "Failed to install Ruby $REQUIRED_RUBY_VERSION"
                print_warning "Please install it manually: rvm install $REQUIRED_RUBY_VERSION"
            fi
        else
            print_info "Ruby version $CURRENT_RUBY_VERSION is compatible"
        fi
    else
        # No Ruby version manager found
        if command -v ruby &> /dev/null; then
            RUBY_VERSION=$(ruby -v | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1)
            print_info "Ruby version: $RUBY_VERSION"
            
            # Check if Ruby version is at least 2.7.0
            if [ "$(version_compare "$RUBY_VERSION" "$MINIMUM_RUBY_VERSION")" = "$MINIMUM_RUBY_VERSION" ]; then
                print_info "Ruby version is compatible"
            else
                print_warning "Ruby version is older than $MINIMUM_RUBY_VERSION"
                print_warning "Please install rbenv or rvm to manage Ruby versions:"
                print_warning "  rbenv: https://github.com/rbenv/rbenv"
                print_warning "  rvm: https://rvm.io/"
            fi
        else
            print_error "Ruby not found. Please install Ruby $MINIMUM_RUBY_VERSION or later"
            exit 1
        fi
    fi
    
    cd "$GEMFILE_DIR"
    
    # Install correct bundler version
    print_info "Checking bundler version..."
    BUNDLER_VERSION=$(grep -A1 "BUNDLED WITH" Gemfile.lock 2>/dev/null | tail -1 | tr -d ' ')
    
    if [ -z "$BUNDLER_VERSION" ]; then
        # No Gemfile.lock, install latest bundler
        print_info "Installing latest bundler..."
        if gem install bundler; then
            print_info "Bundler installed successfully"
        else
            print_warning "Failed to install bundler"
        fi
    else
        # Install specific bundler version
        print_info "Installing bundler version $BUNDLER_VERSION..."
        if gem install bundler -v "$BUNDLER_VERSION"; then
            print_info "Bundler $BUNDLER_VERSION installed successfully"
        else
            # Fallback to any bundler 2.x
            print_warning "Failed to install bundler $BUNDLER_VERSION, trying bundler 2.x"
            if gem install bundler -v '~> 2.0'; then
                print_info "Bundler 2.x installed successfully"
            else
                print_warning "Failed to install bundler"
            fi
        fi
    fi
    
    if command -v bundle &> /dev/null; then
        if bundle install; then
            cd - > /dev/null
            print_info "✅ Ruby dependencies installed"
        else
            cd - > /dev/null
            print_warning "Failed to install Ruby dependencies"
            print_warning "You can install them manually later:"
            print_warning "  cd $GEMFILE_DIR && bundle install"
        fi
    else
        # Try to install bundler
        if command -v gem &> /dev/null; then
            print_info "Installing bundler..."
            if gem install bundler; then
                if bundle install; then
                    cd - > /dev/null
                    print_info "✅ Ruby dependencies installed"
                else
                    cd - > /dev/null
                    print_warning "Failed to install Ruby dependencies"
                fi
            else
                cd - > /dev/null
                print_warning "Failed to install bundler"
            fi
        else
            cd - > /dev/null
            print_warning "Ruby not found. Please install Ruby first"
        fi
    fi
elif [ "$SKIP_BUNDLE" = true ]; then
    print_info "Skipping bundle install as requested"
fi

# Create initial config.json
CONFIG_CREATED=false

if [ -f "kjui_tools/bin/kjui" ]; then
    KJUI_BIN="kjui_tools/bin/kjui"
    print_info "Checking for Android project..."
    
    # Search for build.gradle or build.gradle.kts files in current and parent directories
    SEARCH_DIR="$(pwd)"
    FOUND_GRADLE=""
    MAX_LEVELS=5
    CURRENT_LEVEL=0
    
    while [ $CURRENT_LEVEL -lt $MAX_LEVELS ] && [ -z "$FOUND_GRADLE" ]; do
        if [ -f "$SEARCH_DIR/build.gradle" ] || [ -f "$SEARCH_DIR/build.gradle.kts" ]; then
            if [ -f "$SEARCH_DIR/settings.gradle" ] || [ -f "$SEARCH_DIR/settings.gradle.kts" ]; then
                FOUND_GRADLE="$SEARCH_DIR"
                print_info "Found Android project: $FOUND_GRADLE"
                break
            fi
        fi
        SEARCH_DIR="$(dirname "$SEARCH_DIR")"
        CURRENT_LEVEL=$((CURRENT_LEVEL + 1))
    done
    
    if [ -n "$FOUND_GRADLE" ]; then
        print_info "Creating initial configuration..."
        # Pass the mode to init command based on installer mode
        INIT_MODE_FLAG=""
        if [ "$MODE" = "xml" ]; then
            INIT_MODE_FLAG="--mode xml"
        else
            INIT_MODE_FLAG="--mode compose"
        fi
        
        if $KJUI_BIN init $INIT_MODE_FLAG 2>/dev/null; then
            CONFIG_CREATED=true
            print_info "✅ Initial configuration created with mode: $MODE"
        else
            print_warning "Failed to create initial configuration"
            print_warning "You can create it manually later with:"
            print_warning "  $KJUI_BIN init $INIT_MODE_FLAG"
        fi
    else
        print_warning "No Android project found in parent directories"
        print_warning "After moving to your Android project directory, run:"
        print_warning "  $KJUI_BIN init"
    fi
fi

# Run dependency installer if available
if [ -f "kjui_tools/bin/install_deps" ]; then
    print_info "Installing Android dependencies..."
    if kjui_tools/bin/install_deps; then
        print_info "✅ Android dependencies configured"
    else
        print_warning "Failed to configure Android dependencies automatically"
        print_warning "You may need to manually add dependencies to your build.gradle"
    fi
fi

print_info ""
print_info "🎉 Installation completed successfully!"
print_info ""
print_info "Installation mode: $MODE"
print_info ""
print_info "Next steps:"

if [ "$MODE" = "xml" ]; then
    if [ -d "kjui_tools" ]; then
        print_info "1. Add kjui_tools/bin to your PATH or use the full path"
        print_info "2. Run 'kjui init' to create XML configuration (if not done)"
        print_info "3. Run 'kjui setup' to set up your Android XML project"
        print_info "4. Run 'kjui generate' to generate ViewBinding code"
        print_info "5. Run 'kjui help' to see available commands"
    fi
else
    if [ -d "kjui_tools" ]; then
        print_info "1. Add kjui_tools/bin to your PATH or use the full path"
        print_info "2. Run 'kjui init' to create configuration (if not done)"
        print_info "3. Run 'kjui setup' to set up your Compose project"
        print_info "4. Run 'kjui generate' to generate Compose code"
        print_info "5. Run 'kjui watch' to enable hot reload"
        print_info "6. Run 'kjui help' to see available commands"
    fi
fi

print_info ""
print_info "For more information, visit: https://github.com/$GITHUB_REPO"