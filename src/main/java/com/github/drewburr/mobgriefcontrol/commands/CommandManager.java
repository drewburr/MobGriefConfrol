package com.github.drewburr.mobgriefcontrol.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import com.github.drewburr.mobgriefcontrol.MobGriefControl;

/**
 * Manages and dispatches commands to their respective handlers
 */
public class CommandManager {

	private final Map<String, CommandHandler> handlers = new HashMap<>();
	private final Map<String, CommandHandler> aliasMap = new HashMap<>();
	private final CommandHandler helpHandler;

	public CommandManager() {
		helpHandler = new HelpCommandHandler();

		// Register all command handlers
		registerHandler(new ConfigCommandHandler());
		registerHandler(new ToggleDebugCommandHandler());
		registerHandler(new ReloadCommandHandler());
		registerHandler(new EndermanGriefCommandHandler());
		registerHandler(new CreeperGriefCommandHandler());
		registerHandler(new GhastGriefCommandHandler());
	}

	/**
	 * Register a command handler
	 */
	private void registerHandler(CommandHandler handler) {
		String commandName = handler.getCommandName().toLowerCase();
		handlers.put(commandName, handler);

		// Register aliases
		for (String alias : handler.getAliases()) {
			aliasMap.put(alias.toLowerCase(), handler);
		}
	}

	/**
	 * Handle a command
	 * @param plugin The plugin instance
	 * @param sender The command sender
	 * @param args The command arguments
	 * @return true if the command was handled successfully
	 */
	public boolean handleCommand(MobGriefControl plugin, CommandSender sender, String[] args) {
		// Show help if no arguments
		if (args.length == 0) {
			return helpHandler.handle(plugin, sender, args);
		}

		String commandName = args[0].toLowerCase();

		// Check handlers first
		CommandHandler handler = handlers.get(commandName);

		// Check aliases if not found
		if (handler == null) {
			handler = aliasMap.get(commandName);
		}

		// Execute handler if found
		if (handler != null) {
			return handler.handle(plugin, sender, args);
		}

		// Unknown command - show help
		return helpHandler.handle(plugin, sender, args);
	}

	/**
	 * Handle tab completion for commands
	 * @param sender The command sender
	 * @param args The command arguments
	 * @return List of tab completions
	 */
	public List<String> handleTabComplete(CommandSender sender, String[] args) {
		List<String> completions = new ArrayList<>();

		if (args.length == 0 || args.length == 1) {
			// Return all command names and aliases that match the partial input
			String partial = args.length == 0 ? "" : args[0].toLowerCase();

			for (CommandHandler handler : handlers.values()) {
				String commandName = handler.getCommandName();
				if (commandName.toLowerCase().startsWith(partial)) {
					completions.add(commandName);
				}
			}

			for (String alias : aliasMap.keySet()) {
				if (alias.toLowerCase().startsWith(partial)) {
					completions.add(alias);
				}
			}
		} else {
			// Get tab completions from the specific handler
			String commandName = args[0].toLowerCase();
			CommandHandler handler = handlers.get(commandName);

			// Check aliases if not found
			if (handler == null) {
				handler = aliasMap.get(commandName);
			}

			if (handler != null) {
				List<String> handlerCompletions = handler.getTabCompletions(args);
				if (handlerCompletions != null) {
					return handlerCompletions;
				}
			}
		}

		return completions;
	}

	/**
	 * Get all registered command names for tab completion
	 */
	public Map<String, CommandHandler> getHandlers() {
		return handlers;
	}

	/**
	 * Get all registered aliases for tab completion
	 */
	public Map<String, CommandHandler> getAliases() {
		return aliasMap;
	}
}
