package com.github.drewburr.mobgriefcontrol.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.drewburr.mobgriefcontrol.MobGriefControl;

/**
 * Handles the reload command
 */
public class ReloadCommandHandler implements CommandHandler {

	@Override
	public boolean handle(MobGriefControl plugin, CommandSender sender, String[] args) {
		if (!sender.hasPermission("mobgriefcontrol.op") && !sender.isOp() &&
			!sender.hasPermission("mobgriefcontrol.admin") && sender instanceof Player) {
			sender.sendMessage("" + plugin.get("mobgriefcontrol.message.no_perm"));
			return false;
		}

		// Use ConfigManager to reload configuration
		plugin.getConfigManager().reload();

		sender.sendMessage(MobGriefControl.THIS_NAME + " has been reloaded");
		return true;
	}

	@Override
	public String getCommandName() {
		return "reload";
	}
}
