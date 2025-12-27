#!/bin/bash

# NoEndermanGrief Build Script
# Compiles the Minecraft plugin and packages it into a JAR file

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="NoEndermanGrief"
MAIN_CLASS="com.github.joelgodofwar.neg.NoEndermanGrief"
SOURCE_DIR="src"
BUILD_DIR="build"
CLASSES_DIR="$BUILD_DIR/classes"
DIST_DIR="dist"
LIB_DIR="lib"

# Get version from plugin.yml
VERSION=$(grep "^version:" plugin.yml | sed 's/version: //')

# Output JAR file name
JAR_FILE="$PROJECT_NAME-$VERSION.jar"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  $PROJECT_NAME Build Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}Version: ${YELLOW}$VERSION${NC}"
echo ""

# Step 1: Clean previous builds
echo -e "${YELLOW}[1/5] Cleaning previous builds...${NC}"
rm -rf "$BUILD_DIR" "$DIST_DIR"
mkdir -p "$CLASSES_DIR" "$DIST_DIR"
echo -e "${GREEN}✓ Clean complete${NC}"
echo ""

# Step 2: Check for dependencies
echo -e "${YELLOW}[2/5] Checking for dependencies...${NC}"

# Check if lib directory exists
if [ ! -d "$LIB_DIR" ]; then
    mkdir -p "$LIB_DIR"
fi

# Check for Paper API JAR
PAPER_API="$LIB_DIR/paper-api.jar"
if [ ! -f "$PAPER_API" ]; then
    echo -e "${YELLOW}Paper API not found. Attempting to download...${NC}"
    # Using a specific version that's guaranteed to exist
    PAPER_URL="https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/1.20.4-R0.1-SNAPSHOT/paper-api-1.20.4-R0.1-SNAPSHOT.jar"

    if command -v wget &> /dev/null; then
        echo -e "  Downloading with wget..."
        wget -O "$PAPER_API" "$PAPER_URL" 2>&1 || {
            echo -e "${RED}Failed to download Paper API${NC}"
            echo -e "${YELLOW}Please manually download:${NC}"
            echo -e "  wget https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/1.20.4-R0.1-SNAPSHOT/paper-api-1.20.4-R0.1-SNAPSHOT.jar -O lib/paper-api.jar"
            exit 1
        }
    elif command -v curl &> /dev/null; then
        echo -e "  Downloading with curl..."
        curl -L -o "$PAPER_API" "$PAPER_URL" || {
            echo -e "${RED}Failed to download Paper API${NC}"
            echo -e "${YELLOW}Please manually download:${NC}"
            echo -e "  curl -L -o lib/paper-api.jar https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/1.20.4-R0.1-SNAPSHOT/paper-api-1.20.4-R0.1-SNAPSHOT.jar"
            exit 1
        }
    else
        echo -e "${RED}Neither wget nor curl found. Cannot download Paper API.${NC}"
        echo -e "${YELLOW}Please manually download from:${NC}"
        echo -e "  $PAPER_URL"
        echo -e "${YELLOW}And save it as: $PAPER_API${NC}"
        exit 1
    fi

    # Verify the download
    if [ -f "$PAPER_API" ] && [ -s "$PAPER_API" ]; then
        echo -e "${GREEN}✓ Paper API downloaded successfully${NC}"
    else
        echo -e "${RED}Download failed or file is empty${NC}"
        rm -f "$PAPER_API"
        exit 1
    fi
fi

# Build classpath from all JARs in lib directory
CLASSPATH=""
for jar in "$LIB_DIR"/*.jar; do
    if [ -f "$jar" ]; then
        if [ -z "$CLASSPATH" ]; then
            CLASSPATH="$jar"
        else
            CLASSPATH="$CLASSPATH:$jar"
        fi
    fi
done

if [ -z "$CLASSPATH" ]; then
    echo -e "${RED}Error: No dependencies found!${NC}"
    exit 1
else
    echo -e "${GREEN}✓ Dependencies ready${NC}"
fi
echo ""

# Step 3: Compile Java source files
echo -e "${YELLOW}[3/5] Compiling Java source files...${NC}"

# Find all Java files
JAVA_FILES=$(find "$SOURCE_DIR" -name "*.java")
FILE_COUNT=$(echo "$JAVA_FILES" | wc -l)

echo -e "  Found ${BLUE}$FILE_COUNT${NC} Java files"

if [ -n "$CLASSPATH" ]; then
    javac -d "$CLASSES_DIR" -cp "$CLASSPATH" -sourcepath "$SOURCE_DIR" $JAVA_FILES
else
    javac -d "$CLASSES_DIR" -sourcepath "$SOURCE_DIR" $JAVA_FILES
fi

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    exit 1
fi
echo ""

# Step 4: Copy resources
echo -e "${YELLOW}[4/5] Copying resources...${NC}"

# Copy plugin.yml
if [ -f "plugin.yml" ]; then
    cp "plugin.yml" "$CLASSES_DIR/"
    echo -e "  ${GREEN}✓${NC} plugin.yml"
fi

# Copy config.yml
if [ -f "config.yml" ]; then
    cp "config.yml" "$CLASSES_DIR/"
    echo -e "  ${GREEN}✓${NC} config.yml"
fi

# Copy lang directory
if [ -d "lang" ]; then
    cp -r "lang" "$CLASSES_DIR/"
    echo -e "  ${GREEN}✓${NC} lang/"
fi

echo -e "${GREEN}✓ Resources copied${NC}"
echo ""

# Step 5: Create JAR file
echo -e "${YELLOW}[5/5] Creating JAR file...${NC}"

cd "$CLASSES_DIR"
jar -cf "../../$DIST_DIR/$JAR_FILE" .
cd ../..

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ JAR created successfully${NC}"
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${GREEN}Build complete!${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo -e "Output: ${YELLOW}$DIST_DIR/$JAR_FILE${NC}"

    # Get file size
    FILE_SIZE=$(du -h "$DIST_DIR/$JAR_FILE" | cut -f1)
    echo -e "Size: ${YELLOW}$FILE_SIZE${NC}"
    echo ""
    echo -e "${GREEN}Installation:${NC}"
    echo -e "  Copy ${YELLOW}$DIST_DIR/$JAR_FILE${NC} to your server's ${BLUE}plugins/${NC} directory"
else
    echo -e "${RED}✗ JAR creation failed${NC}"
    exit 1
fi
