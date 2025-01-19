package com.ar.askgaming.pvpthingsnpcaddon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.citizensnpcs.api.npc.NPC;

public class PlayerDeathListener implements Listener{

    private NpcAddon plugin;
    public PlayerDeathListener(NpcAddon plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity() instanceof NPC){
            NPC npc = (NPC) event.getEntity();
            npc.destroy();

        }
    }

}
