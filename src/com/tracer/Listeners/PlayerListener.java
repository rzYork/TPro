package com.tracer.Listeners;

import com.mojang.serialization.RecordBuilder;
import com.tracer.TPro;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

public class PlayerListener implements Listener {
    private TPro plugin;

    public PlayerListener(TPro plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        YamlConfiguration config = plugin.getPlayerDataConfig(p);
        if (config == null) {
            plugin.getLogger().info("CREATED DATA CONFIG OF PLAYER " + e.getPlayer().getUniqueId() + "____" + e.getPlayer().getName() + " FAILED");
        }
    }

    @EventHandler
    public void onHeldNew(PlayerItemHeldEvent e) {

        ItemStack itemToHeld = e.getPlayer().getInventory().getContents()[e.getNewSlot()];
        if (!plugin.changePro(e.getPlayer(), itemToHeld))
            e.setCancelled(true);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e){
        if (!plugin.changePro(e.getPlayer(), e.getMainHandItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickItem(InventoryClickEvent e){
        if(e.getWhoClicked() instanceof Player){
            Player p=(Player)e.getWhoClicked();
            if(e.getSlot()==p.getInventory().getHeldItemSlot()){
                if(!plugin.changePro(p,e.getCursor())){
                    e.setCancelled(true);
                }
            }
        }
    }


}
