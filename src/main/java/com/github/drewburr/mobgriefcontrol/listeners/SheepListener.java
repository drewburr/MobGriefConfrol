package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class SheepListener implements Listener {
	private final MobGriefControl plugin;

	public SheepListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.SHEEP) {
				if (!plugin.getConfig().getBoolean("do_sheep_eat_grass", true)) {
					event.setCancelled(true);
				}
				MobGriefControl.LOGGER.debug("Sheep attempted to eat grass at " + event.getBlock().getLocation());
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_SHEEP_GRIEF).error(exception));
		}
	}
}
