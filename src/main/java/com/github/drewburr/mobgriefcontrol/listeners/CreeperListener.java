package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class CreeperListener implements Listener {
	private final MobGriefControl plugin;

	public CreeperListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.CREEPER) {
				if (!plugin.getConfig().getBoolean("do_creeper_explode", true)) {
					event.blockList().clear();
				}
				MobGriefControl.LOGGER.debug("" + plugin.get("mobgriefcontrol.entity.creeper.explode")
						+ event.getLocation().getBlockX() + ", " + event.getLocation().getBlockZ());
				return;
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_CREEPER_GRIEF).error(exception));
		}
	}
}
