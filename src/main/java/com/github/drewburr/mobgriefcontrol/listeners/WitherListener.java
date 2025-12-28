package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class WitherListener implements Listener {
	private final MobGriefControl plugin;

	public WitherListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.WITHER ||
					event.getEntity().getType() == EntityType.WITHER_SKULL) {
				if (!plugin.getConfig().getBoolean("do_wither_explode", true)) {
					event.blockList().clear();
				}
				MobGriefControl.LOGGER.debug("Wither attempted to explode at " + event.getLocation());
				return;
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_WITHER_GRIEF).error(exception));
		}
	}
}
