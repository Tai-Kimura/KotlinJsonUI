#!/bin/bash

# Test script for kjui_tools build command

echo "Testing kjui_tools build command..."
echo "=================================="

# Set up Ruby path for kjui_tools
export PATH="/usr/bin/ruby:$PATH"

# Navigate to kjui_tools directory
cd ../kjui_tools

# Run the build command from the sample-app directory
echo "Running: ruby bin/kjui build"
ruby -I lib bin/kjui build --clean

echo ""
echo "Build complete! Check the generated files in:"
echo "  - app/src/main/kotlin/com/example/kotlinjsonui/sample/data/"
echo "  - app/src/main/kotlin/com/example/kotlinjsonui/sample/generated/"