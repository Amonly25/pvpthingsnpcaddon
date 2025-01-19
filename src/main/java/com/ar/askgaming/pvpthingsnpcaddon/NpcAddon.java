package com.ar.askgaming.pvpthingsnpcaddon;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.pvpthings.PvpThings;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.goals.TargetNearbyEntityGoal;
import net.citizensnpcs.api.ai.goals.WanderGoal;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.AttributeTrait;

public class NpcAddon extends JavaPlugin {

    private PvpThings pvpThings;

    public PvpThings getPvpThings() {
        if (pvpThings == null) {
            pvpThings = (PvpThings) getServer().getPluginManager().getPlugin("PvpThings");
        }
        return pvpThings;
    }

    private CheckNpcTask task;

    public void onEnable() {
        
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        new NPCDespawnListener(this);
        new PlayerDeathListener(this);

        if (getServer().getPluginManager().getPlugin("Citizens") == null) {
            getLogger().severe("Citizens plugin not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (getServer().getPluginManager().getPlugin("PvpThings") == null) {
            getLogger().severe("PvpThings plugin not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            pvpThings = (PvpThings) getServer().getPluginManager().getPlugin("PvpThings");
        }

        task = new CheckNpcTask(this);
        task.runTaskTimer(this, 0, 20*60);
    }
    public void onDisable() {
        for (NPC npc : getNpcAlives().keySet()) {

            ((Player) npc.getEntity()).setHealth(0);       
            npc.destroy();
        }
    }

    private HashMap<Player, NPC> npcPlayerLink = new LinkedHashMap<>();
    private HashMap<NPC, Long> npcAlives = new HashMap<>();

    public HashMap<Player, NPC> getNpcPlayerLink() {
        return npcPlayerLink;
    }
    public HashMap<NPC, Long> getNpcAlives() {
        return npcAlives;
    }
    public void createNpcPlayerLink(Player p){

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC npc = registry.createNPC(EntityType.PLAYER, p.getName());

        npc.spawn(p.getLocation());
        npc.setName(p.getName() + " (NPC)");
        
        npc.getOrAddTrait(net.citizensnpcs.api.trait.trait.Inventory.class).setContents(p.getInventory().getContents());
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.OFF_HAND, p.getInventory().getItemInOffHand());
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, p.getInventory().getHelmet());
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, p.getInventory().getChestplate());
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, p.getInventory().getLeggings());
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, p.getInventory().getBoots());
        npc.getOrAddTrait(AttributeTrait.class).setAttributeValue(Attribute.MOVEMENT_SPEED, 0.50);
        npc.getOrAddTrait(AttributeTrait.class).setAttributeValue(Attribute.MAX_HEALTH, 20);
        double dmg = 0;
        if (p.getHealth() <= 1){
            ((Damageable) npc.getEntity()).setHealth(1);
        } else {
            ((Damageable) npc.getEntity()).setHealth(p.getHealth());
        }
        
        npc.data().setPersistent(NPC.Metadata.DEFAULT_PROTECTED,false);
        npc.data().setPersistent(NPC.Metadata.DROPS_ITEMS,true);

        WanderGoal w = WanderGoal.builder(npc).build();
        Set<EntityType> targetTypes = Set.of(EntityType.PLAYER, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER, EntityType.ENDERMAN);
        TargetNearbyEntityGoal g = TargetNearbyEntityGoal.builder(npc).aggressive(true).radius(10).targets(targetTypes).build();

        npc.getDefaultGoalController().addGoal(w, 1);
        npc.getDefaultGoalController().addGoal(g, 1);

        npcPlayerLink.put(p, npc);
        npcAlives.put(npc, System.currentTimeMillis());
        
    }

    public void switchFromNpc(Player p) {

        NPC npc = getNpcPlayerLink().get(p);
        p.setHealth(((Damageable) npc.getEntity()).getHealth());
        npc.data().setPersistent(NPC.Metadata.DROPS_ITEMS,false);   
        Location l = npc.getEntity().getLocation();
 
        p.getInventory().setContents(npc.getOrAddTrait(net.citizensnpcs.api.trait.trait.Inventory.class).getContents());
        p.getInventory().setItemInOffHand(npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.OFF_HAND));
        p.getInventory().setHelmet(npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.HELMET));
        p.getInventory().setChestplate(npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.CHESTPLATE));
        p.getInventory().setLeggings(npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.LEGGINGS));
        p.getInventory().setBoots(npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.BOOTS));

        npc.destroy();
        npcAlives.remove(npc);
        npcPlayerLink.remove(p);

        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                p.teleport(l);
            }
        }, 20L);

    }
    
}