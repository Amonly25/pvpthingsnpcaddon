package com.ar.askgaming.pvpthingsnpcaddon;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;

public class NPCDespawnListener implements Listener{

    private NpcAddon plugin;
    public NPCDespawnListener(NpcAddon plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onNPCDespawn(NPCDespawnEvent e) {
        NPC npc = e.getNPC();
        for (NPC n : plugin.getNpcPlayerLink().values()) {
            if (n.equals(npc)) {
                if (npc.getEntity().isDead()) {
                    return;

                }
                ((Damageable) npc.getEntity()).setHealth(0);
                
            }
        }
    }
}
