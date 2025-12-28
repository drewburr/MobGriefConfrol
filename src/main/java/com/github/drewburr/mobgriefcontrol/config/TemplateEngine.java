package com.github.drewburr.mobgriefcontrol.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;

/**
 * Simple template engine for processing migration templates
 * Supports Jinja-like syntax: {{ old.key | default(value) }}
 */
public class TemplateEngine {

	// Pattern to match {{ expression }}
	private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{\\s*(.+?)\\s*\\}\\}");

	// Pattern to parse expressions like: old.key | default(value)
	private static final Pattern EXPRESSION_PATTERN = Pattern
			.compile("old\\.([\\w.]+)(?:\\s*\\|\\s*default\\((.+?)\\))?");

	/**
	 * Process a template string by replacing placeholders with values from
	 * oldConfig
	 */
	public static String processTemplate(String template, YamlConfiguration oldConfig) {
		if (template == null) {
			return null;
		}

		StringBuffer result = new StringBuffer();
		Matcher matcher = TEMPLATE_PATTERN.matcher(template);

		while (matcher.find()) {
			String expression = matcher.group(1).trim();
			Object value = evaluateExpression(expression, oldConfig);

			// Convert value to string representation
			String replacement = valueToString(value);
			matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
		}

		matcher.appendTail(result);
		return result.toString();
	}

	/**
	 * Evaluate an expression like "old.key | default(value)"
	 */
	private static Object evaluateExpression(String expression, YamlConfiguration oldConfig) {
		Matcher matcher = EXPRESSION_PATTERN.matcher(expression);

		if (matcher.matches()) {
			String key = matcher.group(1);
			String defaultValue = matcher.group(2);

			// Get value from old config
			Object value = oldConfig.get(key);

			// If not found, use default
			if (value == null && defaultValue != null) {
				value = parseValue(defaultValue.trim());
			}

			return value;
		}

		// If pattern doesn't match, return expression as-is
		MobGriefControl.LOGGER.warn("Could not parse template expression: " + expression);
		return expression;
	}

	/**
	 * Parse a default value string into appropriate type
	 */
	private static Object parseValue(String value) {
		// Remove quotes if present
		if ((value.startsWith("\"") && value.endsWith("\"")) ||
				(value.startsWith("'") && value.endsWith("'"))) {
			return value.substring(1, value.length() - 1);
		}

		// Parse boolean
		if (value.equalsIgnoreCase("true")) {
			return true;
		}
		if (value.equalsIgnoreCase("false")) {
			return false;
		}

		// Try to parse as number
		try {
			if (value.contains(".")) {
				return Double.parseDouble(value);
			} else {
				return Integer.parseInt(value);
			}
		} catch (NumberFormatException e) {
			// Not a number, return as string
		}

		return value;
	}

	/**
	 * Convert a value to its string representation for YAML
	 */
	private static String valueToString(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof String) {
			// Quote strings that contain special characters
			String str = (String) value;
			if (str.contains(":") || str.contains("#") || str.contains("'") || str.contains("\"")) {
				return "\"" + str.replace("\"", "\\\"") + "\"";
			}
			return str;
		}
		return value.toString();
	}
}
