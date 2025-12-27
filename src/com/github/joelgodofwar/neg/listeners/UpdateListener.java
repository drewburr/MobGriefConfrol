package com.github.joelgodofwar.neg.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.joelgodofwar.neg.NoEndermanGrief;
import com.github.joelgodofwar.neg.util.Utils;

public class UpdateListener implements Listener {
	private final NoEndermanGrief plugin;

	public UpdateListener(NoEndermanGrief plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Send update notification if available and player has permission
		if(plugin.UpdateAvailable && (player.isOp() || player.hasPermission("noendermangrief.showUpdateAvailable") || player.hasPermission("noendermangrief.admin"))) {
			String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";
			links = links.replace("<DownloadLink>", plugin.DownloadLink).replace("<Download>", plugin.get("neg.version.download"))
					.replace("<Donate>", plugin.get("neg.version.donate")).replace("<please_update>", plugin.get("neg.version.please_update"))
					.replace("<Donate_msg>", plugin.get("neg.version.donate.message")).replace("<Notes>", plugin.get("neg.version.notes"))
					.replace("<Notes_msg>", plugin.get("neg.version.notes.message"));
			String versions = "" + ChatColor.GRAY + plugin.get("neg.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + plugin.get("neg.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
			player.sendMessage("" + ChatColor.GRAY + plugin.get("neg.version.message").toString().replace("<MyPlugin>", ChatColor.GOLD + plugin.THIS_NAME + ChatColor.GRAY));
			Utils.sendJson(player, links);
			player.sendMessage(versions.replace("{nVers}", plugin.UCnewVers).replace("{oVers}", plugin.UColdVers));
		}

		// Special greeting for plugin author
		if(player.getDisplayName().equals("JoelYahwehOfWar") || player.getDisplayName().equals("JoelGodOfWar")) {
			player.sendMessage(plugin.THIS_NAME + " " + plugin.THIS_VERSION + " Hello father!");
		}
	}
}
