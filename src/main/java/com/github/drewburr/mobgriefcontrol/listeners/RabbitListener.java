package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class RabbitListener implements Listener {
	private final MobGriefControl plugin;

	public RabbitListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.RABBIT) {
				if (!plugin.getConfig().getBoolean("do_rabbit_eat_crops", true)) {
					event.setCancelled(true);
				}
				MobGriefControl.LOGGER.debug("Rabbit attempted to eat crops at " + event.getBlock().getLocation());
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_RABBIT_GRIEF).error(exception));
		}
	}
}
