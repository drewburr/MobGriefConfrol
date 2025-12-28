package com.github.joelgodofwar.neg.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.joelgodofwar.neg.NoEndermanGrief;
import com.github.joelgodofwar.neg.common.PluginLibrary;
import com.github.joelgodofwar.neg.common.error.Report;

public class GhastListener implements Listener {
	private final NoEndermanGrief plugin;

	public GhastListener(NoEndermanGrief plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		try {
			if ((event.getEntity().getType() == EntityType.FIREBALL) && (((Fireball) event.getEntity()).getShooter() instanceof Ghast)) {
				if(!plugin.getConfig().getBoolean("ghast_grief", false)){
					Entity fireball = event.getEntity();
					((Fireball) fireball).setIsIncendiary(false);
					((Fireball) fireball).setYield(0F);
					event.setCancelled(true);
				}
				NoEndermanGrief.LOGGER.debug("" + plugin.get("neg.entity.ghast.explode") + event.getLocation().getBlockX() + ", " + event.getLocation().getBlockZ());
				return;
			}
		} catch (Exception exception) {
			plugin.reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.ERROR_HANDLING_GHAST_GRIEF).error(exception));
		}
	}
}
