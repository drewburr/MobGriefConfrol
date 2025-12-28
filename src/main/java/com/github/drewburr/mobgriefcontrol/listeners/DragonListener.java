package com.github.drewburr.mobgriefcontrol.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.drewburr.mobgriefcontrol.MobGriefControl;
import com.github.drewburr.mobgriefcontrol.common.PluginLibrary;
import com.github.drewburr.mobgriefcontrol.common.error.Report;

public class DragonListener implements Listener {
    private final MobGriefControl plugin;

    public DragonListener(MobGriefControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        try {
            if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
                if (!plugin.getConfig().getBoolean("do_dragon_destroy", true)) {
                    event.setCancelled(true);
                }
                MobGriefControl.LOGGER
                        .debug("Ender Dragon attempted to destroy block at " + event.getBlock().getLocation());
            }
        } catch (Exception exception) {
            MobGriefControl.reporter.reportDetailed(plugin,
                    Report.newBuilder(PluginLibrary.ERROR_HANDLING_DRAGON_GRIEF).error(exception));
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        try {
            if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
                if (!plugin.getConfig().getBoolean("do_dragon_destroy", true)) {
                    event.blockList().clear();
                }
                MobGriefControl.LOGGER.debug("Ender Dragon explosion at " + event.getLocation());
            }
        } catch (Exception exception) {
            MobGriefControl.reporter.reportDetailed(plugin,
                    Report.newBuilder(PluginLibrary.ERROR_HANDLING_DRAGON_GRIEF).error(exception));
        }
    }
}
