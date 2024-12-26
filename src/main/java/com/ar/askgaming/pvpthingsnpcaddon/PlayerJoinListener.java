package com.ar.askgaming.pvpthingsnpcaddon;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.citizensnpcs.api.npc.NPC;

public class PlayerJoinListener implements Listener{

    private NpcAddon plugin;
    public PlayerJoinListener(NpcAddon plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        
        if (plugin.getNpcPlayerLink().get(p) != null){
            NPC npc = plugin.getNpcPlayerLink().get(p);
            if (npc.isSpawned()){
                plugin.switchFromNpc(p);
            } else {
                p.setHealth(0);
                plugin.getNpcAlives().remove(npc);
                plugin.getNpcPlayerLink().remove(p);
                npc.destroy();
            }
        }
    }
}
