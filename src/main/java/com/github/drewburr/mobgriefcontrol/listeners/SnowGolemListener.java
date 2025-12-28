package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class SnowGolemListener implements Listener {
	private final MobGriefControl plugin;

	public SnowGolemListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.SNOWMAN) {
				if (!plugin.getConfig().getBoolean("do_snowgolem_snow_trail", true)) {
					event.setCancelled(true);
				}
				MobGriefControl.LOGGER
						.debug("Snow Golem attempted to create snow at " + event.getBlock().getLocation());
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_SNOWGOLEM_GRIEF).error(exception));
		}
	}
}
