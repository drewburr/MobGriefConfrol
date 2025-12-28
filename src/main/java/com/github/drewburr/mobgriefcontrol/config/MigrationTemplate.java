package com.github.drewburr.mobgriefcontrol.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;

/**
 * Represents a migration template with Jinja-like syntax
 * Template files use {{ old.key | default(value) }} syntax
 */
public class MigrationTemplate {
	private final String version;
	private final String template;

	public MigrationTemplate(String version, String template) {
		this.version = version;
		this.template = template;
	}

	/**
	 * Load a migration template from a YAML file in resources
	 */
	public static MigrationTemplate load(InputStream inputStream, String filename) {
		try {
			// Read the entire template file as a string
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String content = reader.lines().collect(Collectors.joining("\n"));
			reader.close();

			// Parse the metadata section to extract version info
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new java.io.StringReader(content));
			String version = yaml.getString("version");

			// Extract the template section (everything after the template: key)
			String template = yaml.getString("template");
			if (template == null) {
				// If no template key, assume entire file after metadata is template
				int templateStart = content.indexOf("template:");
				if (templateStart >= 0) {
					template = content.substring(templateStart + 9).trim(); // Skip "template:"
				} else {
					template = content;
				}
			}

			MobGriefControl.LOGGER.log("Loaded migration template: " + filename +
				" (version: " + version + ")");

			return new MigrationTemplate(version, template);
		} catch (Exception e) {
			MobGriefControl.LOGGER.warn("Failed to load migration template: " + filename);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Execute this migration by processing the template
	 */
	public void execute(YamlConfiguration oldConfig, YamlConfiguration newConfig) {
		MobGriefControl.LOGGER.log("Applying migration to version: " + version);

		// Process the template with old config values
		String processed = TemplateEngine.processTemplate(template, oldConfig);

		// Parse the result as YAML and copy to newConfig
		try {
			YamlConfiguration result = YamlConfiguration.loadConfiguration(new java.io.StringReader(processed));

			// Copy all keys from result to newConfig
			for (String key : result.getKeys(true)) {
				if (!result.isConfigurationSection(key)) {
					newConfig.set(key, result.get(key));
				}
			}
		} catch (Exception e) {
			MobGriefControl.LOGGER.warn("Failed to parse processed template");
			e.printStackTrace();
		}
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "Migration to " + version;
	}
}
