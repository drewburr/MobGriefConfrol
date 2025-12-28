# Quick Start: Migration Templates

## Example 1: Simple Migration (2.0.1)

If you just need to add one new setting, create `migration_2.0.1.yml`:

```yaml
version: "2.0.1"

template: |
  version: "2.0.1"
  debug: {{ old.debug | default(false) }}
  lang: {{ old.lang | default("en_US") }}

  # All existing mob controls
  do_enderman_pickup: {{ old.do_enderman_pickup | default(true) }}
  do_creeper_explode: {{ old.do_creeper_explode | default(true) }}
  # ... all other settings ...

  # NEW: Add this one new setting
  do_new_mob_behavior: {{ old.do_new_mob_behavior | default(true) }}
```

## Example 2: Rename a Setting (2.0.2)

To rename `old_name` to `new_name`, create `migration_2.0.2.yml`:

```yaml
version: "2.0.2"

template: |
  version: "2.0.2"

  # Reference the OLD name, but write it with the NEW name
  new_name: {{ old.old_name | default(true) }}

  # Keep everything else
  debug: {{ old.debug | default(false) }}
  # ...
```

## Example 3: Remove a Setting

Just don't include it in the template! If it's not in the template, it won't be in the new config.

## Example 4: Change Default Value

```yaml
# Old default was false, new default is true
new_feature: {{ old.new_feature | default(true) }}
```

## The Process

When you upgrade from 2.0.0 → 2.0.5:

1. System automatically scans the JAR for all `migration_*.yml` files in the migrations folder
2. Finds templates: 2.0.1, 2.0.2, 2.0.3, 2.0.4, 2.0.5
3. Runs 2.0.1 migration on your 2.0.0 config → produces 2.0.1 config
4. Runs 2.0.2 migration on that 2.0.1 config → produces 2.0.2 config
5. Continues through all versions in order
6. Final result: Your config is now 2.0.5 with all changes applied!

**No code changes needed!** Just drop your `migration_X.Y.Z.yml` file in the migrations folder and it's automatically discovered.
