package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class SilverfishListener implements Listener {
	private final MobGriefControl plugin;

	public SilverfishListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.SILVERFISH) {
				if (!plugin.getConfig().getBoolean("do_silverfish_infest_blocks", true)) {
					event.setCancelled(true);
					// Stop the silverfish from repeatedly attempting to infest blocks
					Silverfish silverfish = (Silverfish) event.getEntity();
					silverfish.getPathfinder().stopPathfinding();
					return;
				}
				MobGriefControl.LOGGER
						.debug("Silverfish attempted to enter/exit block at " + event.getBlock().getLocation());
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_SILVERFISH_GRIEF).error(exception));
		}
	}
}
