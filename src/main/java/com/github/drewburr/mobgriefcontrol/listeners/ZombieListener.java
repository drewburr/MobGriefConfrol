package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class ZombieListener implements Listener {
	private final MobGriefControl plugin;

	public ZombieListener(MobGriefControl plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityBreakDoor(EntityBreakDoorEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.ZOMBIE ||
					event.getEntity().getType() == EntityType.ZOMBIE_VILLAGER ||
					event.getEntity().getType() == EntityType.ZOMBIFIED_PIGLIN) {
				if (!plugin.getConfig().getBoolean("do_zombie_break_doors", true)) {
					event.setCancelled(true);
				}
				MobGriefControl.LOGGER.debug("Zombie attempted to break door at " + event.getBlock().getLocation());
			}
		} catch (Exception exception) {
			MobGriefControl.reporter.reportDetailed(plugin,
					Report.newBuilder(PluginLibrary.ERROR_HANDLING_ZOMBIE_GRIEF).error(exception));
		}
	}
}
