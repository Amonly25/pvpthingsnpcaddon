package com.ar.askgaming.pvpthingsnpcaddon;

import org.bukkit.entity.Damageable;
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
            if (System.currentTimeMillis() - plugin.getNpcAlives().get(npc) > 1000*60*5) {
                ((Damageable) npc.getEntity()).setHealth(0);
                npc.destroy();
                plugin.getNpcAlives().remove(npc);
                plugin.getNpcPlayerLink().entrySet().removeIf(entry -> entry.getValue().equals(npc));
            }
        }
    }
}
