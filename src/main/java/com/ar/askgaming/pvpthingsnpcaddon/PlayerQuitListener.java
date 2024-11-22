package com.ar.askgaming.pvpthingsnpcaddon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerQuitListener implements Listener {

    private NpcAddon plugin;
    public PlayerQuitListener(NpcAddon plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (plugin.getPvpThings().getPvpManager().getPvpPlayer(player).isInCombat()) {
            plugin.createNpcPlayerLink(player);

            player.getEquipment().clear();
            player.getInventory().clear();
            Bukkit.broadcastMessage("El jugador " + player.getName() + " ha abandonado el juego, pero su NPC sigue en combate");
        }        
    }
}
