package com.ar.askgaming.pvpthingsnpcaddon;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.npc.NPC;

public class CheckNpcTask extends BukkitRunnable{

    private NpcAddon plugin;
    public CheckNpcTask(NpcAddon plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        for (NPC npc : plugin.getNpcAlives().keySet()) {
            //long timetodespawn = 1000*60*5 - (System.currentTimeMillis() - plugin.getNpcAlives().get(npc));
          
            if (System.currentTimeMillis() - plugin.getNpcAlives().get(npc) > 1000*60*5) {
                ((Player) npc.getEntity()).setHealth(0);  
                npc.destroy();
                plugin.getNpcAlives().remove(npc);
                plugin.getNpcPlayerLink().entrySet().removeIf(entry -> entry.getValue().equals(npc));
            }
        }
    }
}
