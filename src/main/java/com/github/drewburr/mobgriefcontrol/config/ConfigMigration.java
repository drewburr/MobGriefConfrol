package com.github.drewburr.mobgriefcontrol.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

/**
 * Handles migration of configuration settings between versions using YAML templates
 */
public class ConfigMigration {

	private static final Map<String, MigrationTemplate> migrationTemplates = new HashMap<>();
	private static boolean templatesLoaded = false;

	/**
	 * Backup current config to old_config.yml
	 */
	public static void backupConfig(MobGriefControl plugin) {
		try {
			File configFile = new File(plugin.getDataFolder(), "config.yml");
			File backupFile = new File(plugin.getDataFolder(), "old_config.yml");

			Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			MobGriefControl.LOGGER.log("Backed up config.yml to old_config.yml");
		} catch (IOException exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
				Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
		}
	}

	/**
	 * Load all migration templates from resources
	 * Automatically discovers migration files by scanning the JAR's migrations directory
	 */
	private static void loadMigrationTemplates(MobGriefControl plugin) {
		if (templatesLoaded) {
			return;
		}

		MobGriefControl.LOGGER.log("Scanning for migration templates...");

		try {
			// Get the plugin JAR file using reflection to access protected method
			File jarFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

			if (jarFile.exists() && jarFile.isFile()) {
				try (JarFile jar = new JarFile(jarFile)) {
					// Iterate through all entries in the JAR
					jar.stream()
						.filter(entry -> !entry.isDirectory())
						.filter(entry -> entry.getName().startsWith("migrations/"))
						.filter(entry -> entry.getName().endsWith(".yml"))
						.filter(entry -> !entry.getName().contains("example"))
						.filter(entry -> !entry.getName().endsWith(".disabled"))
						.forEach(entry -> {
							String filename = entry.getName();
							try {
								InputStream stream = plugin.getResource(filename);
								if (stream != null) {
									MigrationTemplate template = MigrationTemplate.load(stream, filename);
									if (template != null) {
										migrationTemplates.put(template.getVersion(), template);
									}
									stream.close();
								}
							} catch (Exception e) {
								MobGriefControl.LOGGER.warn("Error loading migration template: " + filename);
								e.printStackTrace();
							}
						});
				}
			}
		} catch (Exception e) {
			MobGriefControl.LOGGER.warn("Error scanning for migration templates: " + e.getMessage());
			e.printStackTrace();
		}

		templatesLoaded = true;
		MobGriefControl.LOGGER.log("Loaded " + migrationTemplates.size() + " migration templates");
	}

	/**
	 * Find all migration templates needed between two versions
	 */
	private static List<MigrationTemplate> findMigrationPath(String fromVersion, String toVersion) {
		List<MigrationTemplate> path = new ArrayList<>();

		// Get all templates and sort by version
		List<MigrationTemplate> allTemplates = new ArrayList<>(migrationTemplates.values());
		allTemplates.sort(Comparator.comparing(t -> new ConfigManager.Version(t.getVersion())));

		// Find templates between fromVersion and toVersion
		ConfigManager.Version from = new ConfigManager.Version(fromVersion);
		ConfigManager.Version to = new ConfigManager.Version(toVersion);

		for (MigrationTemplate template : allTemplates) {
			ConfigManager.Version templateVersion = new ConfigManager.Version(template.getVersion());
			if (templateVersion.compareTo(from) > 0 && templateVersion.compareTo(to) <= 0) {
				path.add(template);
			}
		}

		MobGriefControl.LOGGER.log("Found " + path.size() + " migrations to apply");
		return path;
	}

	/**
	 * Migrate settings from old config to new config using YAML templates
	 */
	public static void migrateSettings(MobGriefControl plugin, YamlConfiguration oldConfig,
			ConfigManager.Version oldVer, ConfigManager.Version newVer) {
		MobGriefControl.LOGGER.log("Migrating config from version " + oldVer + " to " + newVer);

		// Load migration templates
		loadMigrationTemplates(plugin);

		// Find migration path
		List<MigrationTemplate> migrations = findMigrationPath(oldVer.toString(), newVer.toString());

		if (migrations.isEmpty()) {
			MobGriefControl.LOGGER.warn("No migration templates found for " + oldVer + " -> " + newVer);
			MobGriefControl.LOGGER.warn("Config migration cannot be performed automatically");
			MobGriefControl.LOGGER.warn("Please manually update your config or delete config.yml to regenerate");

			// Copy existing keys as-is (no automatic migration for breaking changes)
			for (String key : oldConfig.getKeys(true)) {
				if (!oldConfig.isConfigurationSection(key)) {
					plugin.getConfig().set(key, oldConfig.get(key));
				}
			}
		} else {
			// Apply migrations in sequence
			YamlConfiguration currentConfig = oldConfig;

			for (MigrationTemplate migration : migrations) {
				YamlConfiguration nextConfig = new YamlConfiguration();
				migration.execute(currentConfig, nextConfig);
				currentConfig = nextConfig;
			}

			// Copy final result to plugin config
			for (String key : currentConfig.getKeys(true)) {
				if (!currentConfig.isConfigurationSection(key)) {
					plugin.getConfig().set(key, currentConfig.get(key));
				}
			}
		}

		MobGriefControl.LOGGER.log("Config migration completed");
	}
}
