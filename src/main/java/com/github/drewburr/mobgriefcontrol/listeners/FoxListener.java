package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class FoxListener implements Listener {
    private final MobGriefControl plugin;

    public FoxListener(MobGriefControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        try {
            if (event.getEntity().getType() == EntityType.FOX) {
                if (!plugin.getConfig().getBoolean("do_fox_pickup_items", true)) {
                    event.setCancelled(true);
                    // Stop the fox from repeatedly attempting to pickup items
                    Fox fox = (Fox) event.getEntity();
                    fox.getPathfinder().stopPathfinding();
                    return;
                }
                MobGriefControl.LOGGER.debug("Fox attempted to pickup item at " + event.getItem().getLocation());
            }
        } catch (Exception exception) {
            MobGriefControl.reporter.reportDetailed(plugin,
                    Report.newBuilder(PluginLibrary.ERROR_HANDLING_FOX_GRIEF).error(exception));
        }
    }
}
