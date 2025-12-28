package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class RavagerListener implements Listener {
	private final MobGriefControl plugin;

	public RavagerListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.RAVAGER) {
				if (!plugin.getConfig().getBoolean("do_ravager_destroy_crops", true)) {
					event.setCancelled(true);
					// Stop the ravager from repeatedly attempting to destroy crops
					Ravager ravager = (Ravager) event.getEntity();
					ravager.getPathfinder().stopPathfinding();
					return;
				}
				MobGriefControl.LOGGER.debug("Ravager attempted to destroy crops at " + event.getBlock().getLocation());
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_RAVAGER_GRIEF).error(exception));
		}
	}
}
