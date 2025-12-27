# Build Instructions

## Prerequisites

This project uses the Paper API (a high-performance fork of Spigot with additional features).

### Automatic Setup

The build script will automatically download the Paper API when you run it for the first time.

### Manual Setup (if needed)

If you prefer to manually download the Paper API:

1. Create a `lib` directory in the project root:

   ```bash
   mkdir lib
   ```

2. Download the Paper API JAR:

   ```bash
   wget https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/1.20.1-R0.1-SNAPSHOT/paper-api-1.20.1-R0.1-SNAPSHOT.jar -O lib/paper-api.jar
   ```

Alternatively, you can use a local Paper server JAR if you have one.

## Building the Plugin

### Option 1: Using the build script (Recommended)

Simply run the build script:

```bash
./build.sh
```

The script will:

1. Clean previous builds
2. Download Paper API (if not present)
3. Compile all Java source files
4. Copy resources (plugin.yml, config.yml, lang/)
5. Package everything into a JAR file

The compiled plugin will be available at: `dist/NoEndermanGrief-<version>.jar`

### Option 2: Manual compilation

If you prefer to compile manually:

```bash
# Create build directories
mkdir -p build/classes dist

# Compile Java files
javac -d build/classes -cp "lib/*" -sourcepath src $(find src -name "*.java")

# Copy resources
cp plugin.yml config.yml build/classes/
cp -r lang build/classes/

# Create JAR
cd build/classes
jar -cf ../../dist/NoEndermanGrief.jar .
cd ../..
```

## Installation

Copy the compiled JAR file from `dist/` to your Minecraft server's `plugins/` directory:

```bash
cp dist/NoEndermanGrief-*.jar /path/to/server/plugins/
```

Then restart or reload your server.

## Troubleshooting

### "Failed to download Paper API"

If automatic download fails, manually download the Paper API:

```bash
wget https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/1.20.1-R0.1-SNAPSHOT/paper-api-1.20.1-R0.1-SNAPSHOT.jar -O lib/paper-api.jar
```

### Compilation errors

Make sure you're using a compatible Paper API version. The plugin is designed for Minecraft 1.14+ and works with Paper servers.

### "command not found: javac"

Install the Java Development Kit (JDK):

- Ubuntu/Debian: `sudo apt install openjdk-17-jdk`
- macOS: `brew install openjdk@17`
- Windows: Download from <https://adoptium.net/>

## Development

After making changes to the code, simply run `./build.sh` again to recompile.

For rapid development, you can create a symlink or use a script to automatically copy the plugin to your test server:

```bash
# Example: Auto-copy after build
./build.sh && cp dist/NoEndermanGrief-*.jar ~/minecraft-server/plugins/
```
