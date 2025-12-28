package com.github.drewburr.mobgriefcontrol.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

/**
 * Manages plugin configuration including creation, migration, and reloading
 */
public class ConfigManager {
	private final MobGriefControl plugin;
	private final String configVersion;

	// Version class for semantic versioning
	public static class Version implements Comparable<Version> {
		int major, minor, patch;

		Version(String version) {
			String[] parts = version.split("\\.");
			major = parts.length > 0 ? parseInt(parts[0]) : 0;
			minor = parts.length > 1 ? parseInt(parts[1]) : 0;
			patch = parts.length > 2 ? parseInt(parts[2]) : 0;
		}

		private int parseInt(String s) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		@Override
		public int compareTo(Version other) {
			if (major != other.major) return Integer.compare(major, other.major);
			if (minor != other.minor) return Integer.compare(minor, other.minor);
			return Integer.compare(patch, other.patch);
		}

		public boolean isMajorUpgrade(Version other) {
			return this.major > other.major;
		}

		public boolean isMinorUpgrade(Version other) {
			return this.major == other.major && this.minor > other.minor;
		}

		@Override
		public String toString() {
			return major + "." + minor + "." + patch;
		}
	}

	public ConfigManager(MobGriefControl plugin) {
		this.plugin = plugin;
		this.configVersion = plugin.getPluginMeta().getVersion();
	}

	/**
	 * Initialize configuration on plugin startup
	 */
	public void initialize() {
		MobGriefControl.LOGGER.log("Checking config file...");

		// Create data folder if needed
		if (!plugin.getDataFolder().exists()) {
			MobGriefControl.LOGGER.log("Data Folder doesn't exist");
			MobGriefControl.LOGGER.log("Creating Data Folder");
			plugin.getDataFolder().mkdirs();
			MobGriefControl.LOGGER.log("Data Folder Created at " + plugin.getDataFolder());
		}

		// Create config if needed
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			MobGriefControl.LOGGER.log("config.yml not found, creating!");
			plugin.saveResource("config.yml", true);
			// Set version in the newly created config
			plugin.reloadConfig();
			plugin.getConfig().set("version", configVersion);
			try {
				plugin.getConfig().save(configFile);
				MobGriefControl.LOGGER.log("Set config version to " + configVersion);
			} catch (IOException e) {
				MobGriefControl.LOGGER.warn("Could not set config version: " + e.getMessage());
			}
		}

		// Check and migrate config version
		checkAndMigrateConfig();
	}

	/**
	 * Check config version and migrate if needed
	 */
	private void checkAndMigrateConfig() {
		MobGriefControl.LOGGER.log("Checking config file version...");
		String currentVersion = plugin.getConfig().getString("version", "1.0.0");
		MobGriefControl.LOGGER.log("Config file version=" + currentVersion + " expected=" + configVersion);

		// If version is null, set it to current plugin version
		if (currentVersion == null || currentVersion.equals("null")) {
			MobGriefControl.LOGGER.log("Config version is null, setting to " + configVersion);
			plugin.getConfig().set("version", configVersion);
			try {
				plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
			} catch (IOException e) {
				MobGriefControl.LOGGER.warn("Could not update config version");
			}
			return;
		}

		if (currentVersion.equalsIgnoreCase(configVersion)) {
			return; // No migration needed
		}

		// Parse versions for comparison
		Version oldVer = new Version(currentVersion);
		Version newVer = new Version(configVersion);

		// Only migrate if config is older than current version
		if (oldVer.compareTo(newVer) >= 0) {
			MobGriefControl.LOGGER.log("Config is same or newer version, no migration needed");
			return;
		}

		// Determine if migration is needed based on version change type
		if (requiresMigration(oldVer, newVer)) {
			MobGriefControl.LOGGER.log("Migration required from " + oldVer + " to " + newVer);
			migrateConfig(currentVersion, oldVer, newVer);
		} else {
			MobGriefControl.LOGGER.log("Patch version change, updating version number only");
			plugin.getConfig().set("version", configVersion);
			try {
				plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
			} catch (IOException e) {
				MobGriefControl.LOGGER.warn("Could not update config version");
			}
		}
	}

	/**
	 * Determine if a migration is required based on version change
	 * Patch versions (x.x.N) don't require migration
	 * Minor and major versions do require migration
	 */
	private boolean requiresMigration(Version oldVer, Version newVer) {
		// Major version change always requires migration
		if (newVer.isMajorUpgrade(oldVer)) {
			return true;
		}
		// Minor version change requires migration
		if (newVer.isMinorUpgrade(oldVer)) {
			return true;
		}
		// Patch version change doesn't require migration
		return false;
	}

	/**
	 * Migrate configuration from old version using version-specific strategy
	 */
	private void migrateConfig(String oldVersionStr, Version oldVer, Version newVer) {
		try {
			// Backup old config
			ConfigMigration.backupConfig(plugin);

			// Load old config
			YamlConfiguration oldConfig = new YamlConfiguration();
			try {
				oldConfig.load(new File(plugin.getDataFolder(), "old_config.yml"));
			} catch (Exception exception) {
				MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
				return;
			}

			// Create new config from resource
			plugin.saveResource("config.yml", true);

			// Reload config
			try {
				plugin.getConfig().load(new File(plugin.getDataFolder(), "config.yml"));
			} catch (Exception exception) {
				MobGriefControl.LOGGER.warn("Could not load config.yml");
				MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
				return;
			}

			// Apply version-specific migration strategy
			ConfigMigration.migrateSettings(plugin, oldConfig, oldVer, newVer);

			// Save migrated config
			try {
				plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
				MobGriefControl.LOGGER.log("config.yml has been updated from " + oldVer + " to " + newVer);
			} catch (IOException exception) {
				MobGriefControl.LOGGER.warn("Could not save old settings to config.yml");
				MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
				Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
		}
	}

	/**
	 * Reload configuration from disk
	 */
	public void reload() {
		MobGriefControl.LOGGER.log("Loading config file...");

		// Check version and migrate if needed
		checkAndMigrateConfig();

		// Load config
		try {
			plugin.getConfig().load(new File(plugin.getDataFolder(), "config.yml"));
		} catch (Exception exception) {
			MobGriefControl.LOGGER.warn("Could not load config.yml");
			MobGriefControl.reporter.reportDetailed(plugin,
				Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
		}

		// Update static fields
		MobGriefControl.debug = plugin.getConfig().getBoolean("debug", false);
		MobGriefControl.daLang = plugin.getConfig().getString("lang", "en_US");

		plugin.reloadConfig();
	}

	/**
	 * Get the expected config version
	 */
	public String getConfigVersion() {
		return configVersion;
	}

	/**
	 * Save the current configuration to disk
	 */
	public void saveConfig() {
		try {
			plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
		} catch (IOException exception) {
			MobGriefControl.LOGGER.warn("Could not save config.yml");
			MobGriefControl.reporter.reportDetailed(plugin,
				Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
		}
	}

	/**
	 * Get a boolean value from config
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return plugin.getConfig().getBoolean(key, defaultValue);
	}

	/**
	 * Get a string value from config
	 */
	public String getString(String key, String defaultValue) {
		return plugin.getConfig().getString(key, defaultValue);
	}

	/**
	 * Set a value in config
	 */
	public void set(String key, Object value) {
		plugin.getConfig().set(key, value);
	}
}
