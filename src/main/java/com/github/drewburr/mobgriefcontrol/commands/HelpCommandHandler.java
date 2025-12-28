package com.github.drewburr.mobgriefcontrol.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.drewburr.mobgriefcontrol.MobGriefControl;

/**
 * Handles the default help command
 */
public class HelpCommandHandler implements CommandHandler {

	@Override
	public boolean handle(MobGriefControl plugin, CommandSender sender, String[] args) {
		if (!sender.hasPermission("mobgriefcontrol.op") &&
			!sender.hasPermission("mobgriefcontrol.admin") &&
			!sender.isOp()) {
			sender.sendMessage("" + plugin.get("mobgriefcontrol.message.no_perm"));
			return false;
		}

		sender.sendMessage("[]===============[" + "MobGriefControl" + "]===============[]");
		sender.sendMessage(" ");

		if (sender.isOp() || sender.hasPermission("mobgriefcontrol.op") ||
			sender.hasPermission("mobgriefcontrol.admin")) {
			sender.sendMessage(" -<[" + " OP Commands " + "}>-");
			sender.sendMessage(" /mobgriefcontrol update - " + plugin.get("mobgriefcontrol.command.update"));
			sender.sendMessage(" /mobgriefcontrol reload - " + plugin.get("mobgriefcontrol.command.reload"));

			if (sender.isOp() || sender.hasPermission("mobgriefcontrol.toggledebug") ||
				!(sender instanceof Player) || sender.hasPermission("mobgriefcontrol.admin")) {
				sender.sendMessage(" /mobgriefcontrol toggledebug - " + plugin.get("mobgriefcontrol.message.debuguse"));
			}
		}

		if (sender.hasPermission("mobgriefcontrol.admin") || !(sender instanceof Player)) {
			sender.sendMessage(" -<[" + " Admin Commands " + "}>-");
			if (sender instanceof Player) {
				sender.sendMessage(" /mobgriefcontrol BOOK - " + plugin.get("mobgriefcontrol.command.book"));
			}
			sender.sendMessage(" /mobgriefcontrol endermangrief true/false - " + plugin.get("mobgriefcontrol.command.endermen"));
			sender.sendMessage(" /mobgriefcontrol creepergrief true/false - " + plugin.get("mobgriefcontrol.command.creeper"));
			sender.sendMessage(" /mobgriefcontrol ghastgrief true/false - " + plugin.get("mobgriefcontrol.command.ghast"));
		}

		sender.sendMessage("[]===============[" + "MobGriefControl" + "]===============[]");
		return true;
	}

	@Override
	public String getCommandName() {
		return "help";
	}
}
