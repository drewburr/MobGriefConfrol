package com.github.joelgodofwar.neg.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.github.joelgodofwar.neg.NoEndermanGrief;
import com.github.joelgodofwar.neg.common.PluginLibrary;
import com.github.joelgodofwar.neg.common.error.Report;

public class EndermanListener implements Listener {
	private final NoEndermanGrief plugin;

	public EndermanListener(NoEndermanGrief plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		try {
			if (event.getEntity() == null) {
				return;
			}

			if (event.getEntity().getType() == EntityType.ENDERMAN) {
				if(!plugin.getConfig().getBoolean("enderman_grief", false)){
					event.setCancelled(true);
				}
				plugin.LOGGER.debug("" + plugin.get("neg.entity.enderman.pickup") + event.getBlock().getType() + " at " + event.getBlock().getLocation());
				return;
			}
		} catch (Exception exception) {
			plugin.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_ENDERMAN_GRIEF).error(exception));
		}
	}
}
