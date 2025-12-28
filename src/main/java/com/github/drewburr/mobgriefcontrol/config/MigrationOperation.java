package com.github.drewburr.mobgriefcontrol.config;

import org.bukkit.configuration.file.YamlConfiguration;
import com.github.drewburr.mobgriefcontrol.MobGriefControl;

/**
 * Represents a single migration operation
 */
public class MigrationOperation {
	private final String type;
	private final String fromKey;
	private final String toKey;
	private final Object value;
	private final Object defaultValue;

	public MigrationOperation(String type, String fromKey, String toKey, Object value, Object defaultValue) {
		this.type = type;
		this.fromKey = fromKey;
		this.toKey = toKey;
		this.value = value;
		this.defaultValue = defaultValue;
	}

	/**
	 * Execute this operation on the configuration
	 */
	public void execute(YamlConfiguration oldConfig, YamlConfiguration newConfig) {
		switch (type.toLowerCase()) {
			case "rename_key":
				renameKey(oldConfig, newConfig);
				break;
			case "copy_key":
				copyKey(oldConfig, newConfig);
				break;
			case "set_default":
				setDefault(oldConfig, newConfig);
				break;
			case "remove_key":
				// Remove operations are handled by not copying to new config
				break;
			case "copy_all":
				copyAll(oldConfig, newConfig);
				break;
			default:
				MobGriefControl.LOGGER.warn("Unknown migration operation: " + type);
		}
	}

	private void renameKey(YamlConfiguration oldConfig, YamlConfiguration newConfig) {
		Object val = oldConfig.get(fromKey);
		if (val == null && defaultValue != null) {
			val = defaultValue;
		}
		if (val != null && toKey != null) {
			newConfig.set(toKey, val);
			MobGriefControl.LOGGER.log("  Renamed: " + fromKey + " -> " + toKey);
		}
	}

	private void copyKey(YamlConfiguration oldConfig, YamlConfiguration newConfig) {
		Object val = oldConfig.get(fromKey);
		if (val == null && defaultValue != null) {
			val = defaultValue;
		}
		if (val != null && toKey != null) {
			newConfig.set(toKey, val);
			MobGriefControl.LOGGER.log("  Copied: " + fromKey + " -> " + toKey);
		}
	}

	private void setDefault(YamlConfiguration oldConfig, YamlConfiguration newConfig) {
		Object val = oldConfig.get(toKey != null ? toKey : fromKey);
		if (val == null) {
			val = value != null ? value : defaultValue;
		}
		if (val != null) {
			String key = toKey != null ? toKey : fromKey;
			newConfig.set(key, val);
			MobGriefControl.LOGGER.log("  Set default: " + key + " = " + val);
		}
	}

	private void copyAll(YamlConfiguration oldConfig, YamlConfiguration newConfig) {
		for (String key : oldConfig.getKeys(true)) {
			if (!oldConfig.isConfigurationSection(key)) {
				newConfig.set(key, oldConfig.get(key));
			}
		}
		MobGriefControl.LOGGER.log("  Copied all keys from old config");
	}

	public String getType() {
		return type;
	}

	public String getFromKey() {
		return fromKey;
	}

	public String getToKey() {
		return toKey;
	}
}
