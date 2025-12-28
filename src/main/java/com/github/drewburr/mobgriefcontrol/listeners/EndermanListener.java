package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class EndermanListener implements Listener {
	private final MobGriefControl plugin;

	public EndermanListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		try {
			if (event.getEntity() == null) {
				return;
			}

			if (event.getEntity().getType() == EntityType.ENDERMAN) {
				if (!plugin.getConfig().getBoolean("do_enderman_pickup", true)) {
					event.setCancelled(true);
				}
				MobGriefControl.LOGGER.debug("" + plugin.get("mobgriefcontrol.entity.enderman.pickup")
						+ event.getBlock().getType() + " at " + event.getBlock().getLocation());
				return;
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_ENDERMAN_GRIEF).error(exception));
		}
	}
}
