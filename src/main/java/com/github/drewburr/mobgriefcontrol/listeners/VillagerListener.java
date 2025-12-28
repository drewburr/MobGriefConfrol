package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class VillagerListener implements Listener {
	private final MobGriefControl plugin;

	public VillagerListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityPickupItem(EntityPickupItemEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.VILLAGER) {
				if (!plugin.getConfig().getBoolean("do_villager_farm", true)) {
					event.setCancelled(true);
					// Stop the villager from repeatedly attempting to pickup items
					Villager villager = (Villager) event.getEntity();
					villager.getPathfinder().stopPathfinding();
					return;
				}
				MobGriefControl.LOGGER.debug("Villager attempted to pickup item at " + event.getItem().getLocation());
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_VILLAGER_GRIEF).error(exception));
		}
	}
}
