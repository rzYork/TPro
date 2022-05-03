package com.tracer.Controller;

import com.tracer.Entity.TPlayerPro;
import com.tracer.TPro;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TProGUI implements Listener {
    private TPro plugin;
    private static final String INVENTORY_PREFIX = "TPRO INSPECT INVENTORY CR v1.0 ";

    public TProGUI(TPro plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getInventory() != null) {
            if (evt.getInventory().getHolder() == null) {
                if (evt.getView().getTitle().startsWith(INVENTORY_PREFIX)) {
                    evt.setCancelled(true);
                    evt.getWhoClicked().closeInventory();
                    ItemStack currentItem = evt.getCurrentItem();
                    if (currentItem != null) {
                        if (currentItem.hasItemMeta()) {
                            ItemMeta itemMeta = currentItem.getItemMeta();
                            if (itemMeta.hasLore()) {
                                for (String s : itemMeta.getLore()) {
                                    evt.getWhoClicked().sendMessage(s);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public Inventory getGUI() {
        int size = plugin.prosLoaded.keySet().size();
        if (size < 9) size = 9;
        if (size > 56) size = 56;
        int itemIndex = 1;
        Inventory inv = plugin.getServer().createInventory(null, size, INVENTORY_PREFIX + System.currentTimeMillis());
        for (Map.Entry<String, TPlayerPro> entry : plugin.prosLoaded.entrySet()) {
            if (itemIndex >= 56) {
                plugin.getLogger().info("TOO MANY PRO! NO PAGE SYSTEM!");
                break;
            }
            TPlayerPro pro = entry.getValue();
            ItemStack stack = new ItemStack(Material.STONE);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName("SKILL NAME: " + pro.getDisplayName());

            LinkedList<String> lore = getLore(pro);
            lore.add("------------------------------");
            lore.add("Levels:");
            HashMap<Integer, List<String>> levelsAward = pro.getLevelsAward();
            HashMap<Integer, Integer> levelsExpNeed = pro.getLevelsExpNeed();
            HashSet<Integer> levelIndex = new HashSet<>(levelsAward.keySet());
            levelIndex.addAll(levelsExpNeed.keySet());
            for (Integer i : levelIndex) {
                lore.add(i + "-> exp: " + levelsExpNeed.get(i));
                lore.add(" _ awards: ");
                List<String> strings = levelsAward.get(i);
                if (strings != null) {
                    for (String s1 : strings) {
                        lore.add(" ------ " + s1);
                    }
                }
            }
            lore.add("------------------------------");
            HashMap<String, String> skillMap = pro.getSkillMap();
            for (Map.Entry<String, String> e : skillMap.entrySet()) {

                lore.add("SKILL NAME: " + e.getKey());
                lore.add("        ---COMMAND: " + e.getValue());
            }
            itemMeta.setLore(lore);
            stack.setItemMeta(itemMeta);
            inv.addItem(stack);
            itemIndex++;
        }
        return inv;
    }

    public LinkedList<String> getLore(TPlayerPro pro) {
        List<String> lore = new LinkedList<>();
        if (!pro.isRootPro()) {
            lore = getLore(pro.getParentPro());
        }
        LinkedList<String> list = new LinkedList<>(lore);

        String builder = "|_" +
                pro.getDisplayName();
        list.add(builder);



        return list;
    }


}
