# Config Migration System

This directory contains YAML-based migration templates using Jinja-like syntax to define how to migrate configuration files between versions.

## How It Works

1. **Automatic Discovery**: The system automatically scans the plugin JAR for all `migration_*.yml` files in this folder at runtime
2. **Migration Templates**: Each `migration_X.Y.Z.yml` file defines how to migrate to version X.Y.Z using template syntax
3. **Chained Migrations**: If migrating from 2.0.0 to 2.0.5, the system will automatically run all intermediate migrations (2.0.1, 2.0.2, 2.0.3, 2.0.4, 2.0.5) in sequence
4. **Smart Execution**: The system only runs migrations when needed based on semantic versioning (major/minor changes, not patches)
5. **Template Processing**: Uses Jinja-like `{{ old.key | default(value) }}` syntax to reference old config values
6. **Zero Maintenance**: Just add new migration files - no code changes required!

## Migration Template Format

```yaml
# Metadata - only the target version is needed
version: "2.0.1"

# Template section - write your new config with placeholders
template: |
  version: "2.0.1"

  # Reference old config values
  debug: {{ old.debug | default(false) }}
  lang: {{ old.lang | default("en_US") }}

  # Rename keys by referencing old name with new name
  new_setting_name: {{ old.old_setting_name | default(true) }}

  # Add new settings with defaults
  new_feature: {{ old.new_feature | default(true) }}

  # Preserve existing settings
  do_creeper_explode: {{ old.do_creeper_explode | default(true) }}
```

## Template Syntax

### Basic Placeholder

```yaml
setting: {{ old.key_name | default(value) }}
```

- `old.key_name` - References a key from the old configuration
- `default(value)` - Optional: Provides a default value if the key doesn't exist
- Supports boolean, number, and string values

### Examples

```yaml
# Boolean with default
enabled: {{ old.enabled | default(true) }}

# String with default
server_name: {{ old.server_name | default("My Server") }}

# Number with default
max_players: {{ old.max_players | default(20) }}

# Reference without default (will use null if not found)
custom_value: {{ old.custom_value }}
```

## Creating a New Migration

1. **Create the file**: `migration_X.Y.Z.yml` in the `src/main/resources/migrations/` folder, where X.Y.Z is the version you're migrating TO

2. **Set metadata**:

   ```yaml
   version: "2.0.2"  # The target version for this migration
   ```

3. **Write the template**: Write the complete new config file structure with placeholders:

   ```yaml
   template: |
     version: "2.0.2"
     # ... rest of config with {{ old.key | default(value) }} placeholders
   ```

4. **Done!** The system will automatically discover your migration file when the plugin loads.

## Example: Adding a New Feature in 2.0.3

Create `migration_2.0.3.yml`:

```yaml
version: "2.0.3"

template: |
  version: "2.0.3"

  # Preserve all existing settings
  debug: {{ old.debug | default(false) }}
  lang: {{ old.lang | default("en_US") }}
  do_creeper_explode: {{ old.do_creeper_explode | default(true) }}
  ...

  # Add new feature with default value
  do_phantom_spawning: {{ old.do_phantom_spawning | default(true) }}
```

## Example: Renaming a Setting in 2.0.4

Create `migration_2.0.4.yml`:

```yaml
version: "2.0.4"

template: |
  version: "2.0.4"

  debug: {{ old.debug | default(false) }}

  # Rename: old_name -> new_name (reference old, write as new)
  better_setting_name: {{ old.old_confusing_name | default(true) }}
```

## Migration Execution

Migrations are executed in version order. For example:

- User has config version **2.0.0**
- Plugin version is **2.0.5**
- System detects migrations needed: 2.0.1, 2.0.2, 2.0.3, 2.0.4, 2.0.5
- Executes each migration in sequence, passing output of each as input to the next

Each migration:

1. Reads the old config values
2. Processes the template by replacing `{{ }}` placeholders
3. Outputs a new config YAML
4. Next migration uses this output as its input

## When Migrations Run

- **Patch versions (X.Y.Z)**: Migrations are optional - usually just bug fixes, no config changes
- **Minor versions (X.Y.0)**: Create migrations when adding features or changing structure
- **Major versions (X.0.0)**: Breaking changes - users must manually migrate or start fresh

The system only runs migrations for major/minor version changes. Patch versions (e.g., 2.0.1 → 2.0.2) don't trigger migration unless a template exists.

## Best Practices

1. **File naming**: Always use `migration_X.Y.Z.yml` format - the filename must match the version
2. **Patch versions (X.Y.Z)**: Usually don't need migrations - just bug fixes
3. **Minor versions (X.Y.0)**: Create migrations when adding new features or changing structure
4. **Major versions (X.0.0)**: Breaking changes - consider requiring manual migration
5. **Test migrations**: Test upgrading from old versions to ensure data preservation
6. **Document changes**: Use comments in the template to explain why changes were made
7. **Include all keys**: Always include all configuration keys in the template, even if unchanged
8. **Version in template**: Always set `version: "X.Y.Z"` in your template output to match the file
9. **Exclude files**: Name files `*.disabled` or include `example` in the name to exclude from auto-discovery

## Advantages Over Operation-Based Migrations

✅ **Readable**: The template looks like actual config, easy to understand
✅ **Complete**: You see the entire output structure at once
✅ **Flexible**: Easy to add, remove, or rename multiple keys
✅ **Self-documenting**: The template itself shows the config structure
✅ **Type-safe**: Default values preserve types (boolean, string, number)
