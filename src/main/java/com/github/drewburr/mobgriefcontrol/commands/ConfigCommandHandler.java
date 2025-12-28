package com.github.drewburr.mobgriefcontrol.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.drewburr.mobgriefcontrol.MobGriefControl;

/**
 * Handles the config command
 */
public class ConfigCommandHandler implements CommandHandler {

	@Override
	public boolean handle(MobGriefControl plugin, CommandSender sender, String[] args) {
		if (!sender.isOp() && !sender.hasPermission("mobgriefcontrol.op") &&
			!sender.hasPermission("mobgriefcontrol.admin") && sender instanceof Player) {
			sender.sendMessage(MobGriefControl.THIS_NAME + " " + plugin.get("mobgriefcontrol.message.noperm"));
			return false;
		}

		sender.sendMessage(MobGriefControl.THIS_NAME + " Current Configuration:");
		sender.sendMessage("  auto_update_check: " + plugin.getConfig().getBoolean("auto_update_check"));
		sender.sendMessage("  debug: " + plugin.getConfig().getBoolean("debug"));
		sender.sendMessage("  lang: " + plugin.getConfig().getString("lang"));
		sender.sendMessage("  console.longpluginname: " + plugin.getConfig().getBoolean("console.longpluginname"));
		sender.sendMessage("  do_enderman_grief: " + plugin.getConfig().getBoolean("do_enderman_grief"));
		sender.sendMessage("  do_creeper_grief: " + plugin.getConfig().getBoolean("do_creeper_grief"));
		sender.sendMessage("  do_ghast_grief: " + plugin.getConfig().getBoolean("do_ghast_grief"));

		return true;
	}

	@Override
	public String getCommandName() {
		return "config";
	}
}
