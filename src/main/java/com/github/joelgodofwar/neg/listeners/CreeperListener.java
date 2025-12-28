package com.github.joelgodofwar.neg.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.joelgodofwar.neg.NoEndermanGrief;
import com.github.joelgodofwar.neg.common.PluginLibrary;
import com.github.joelgodofwar.neg.common.error.Report;

public class CreeperListener implements Listener {
	private final NoEndermanGrief plugin;

	public CreeperListener(NoEndermanGrief plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		try {
			if (event.getEntity().getType() == EntityType.CREEPER) {
				if(!plugin.getConfig().getBoolean("creeper_grief", false)){
					event.blockList().clear();
				}
				NoEndermanGrief.LOGGER.debug("" + plugin.get("neg.entity.creeper.explode") + event.getLocation().getBlockX() + ", " + event.getLocation().getBlockZ());
				return;
			}
		} catch (Exception exception) {
			plugin.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_CREEPER_GRIEF).error(exception));
		}
	}
}
