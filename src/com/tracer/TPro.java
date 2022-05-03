package com.tracer;

import com.tracer.Controller.TProGUI;
import com.tracer.Entity.TPlayerPro;
import com.tracer.Excutor.TProCommandExecutor;
import com.tracer.Expansion.TProExpansion;
import com.tracer.Listeners.PlayerListener;
import com.tracer0219.tpvp.tpvp.TPVP;
import org.bukkit.Bukkit;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;

public class TPro extends JavaPlugin {
    public File configFile = new File(this.getDataFolder(), "config.yml");
    public File playerDataFolder = new File(this.getDataFolder(), "players");
    public PlayerListener playerListener;
    public TProGUI tProGUIListener;
    public static int defaultExpToLevelUp;
    public static int maxLevel;
    private FileConfiguration config;
    public HashMap<String, TPlayerPro> prosLoaded = new HashMap<>();
    public static HashMap<String, String> itemToProMap = new HashMap<>();
    public static HashMap<Player, String> playerToProMap = new HashMap<>();

    public File getPlayerDataFile(Player p) {
        File playerConfigFile = new File(playerDataFolder, p.getUniqueId() + ".yml");
        if (!playerConfigFile.exists()) {
            try {
                playerConfigFile.createNewFile();
            } catch (IOException e) {
                getLogger().info("CREATE PLAYER DATA FILE OF " + p.getName() + " - " + playerConfigFile.getName() + " FAILED!!!!!!");
                return null;
            }

        }
        return playerConfigFile;
    }

    public YamlConfiguration getPlayerDataConfig(Player p) {

        File playerConfigFile = getPlayerDataFile(p);
        if (playerConfigFile != null) {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerConfigFile);
            if (config.get("uuid") == null)
                config.set("uuid", p.getUniqueId().toString());
            if (config.get("name") == null)
                config.set("name", p.getName());
            if (config.get("access_time") == null)
                config.set("access_time", System.currentTimeMillis());
            if (config.get("pros") == null)
                config.set("pros", new ArrayList<>());
            for (Map.Entry<String, TPlayerPro> proEntry : prosLoaded.entrySet()) {

                String path = "pros." + proEntry.getKey();
                if (config.get(path) == null) {
                    config.set(path, "0_0");
                }
            }
            try {
                config.save(playerConfigFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return config;

        }
        return null;

    }

    public boolean savePlayerConfig(Player p, YamlConfiguration config) {
        File playerDataFile = getPlayerDataFile(p);
        if (playerDataFile == null) return false;
        try {
            config.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public TPlayerPro getPlayerPro(Player p) {
        TPro pro;
        String s = playerToProMap.get(p);
        if (s == null || s.isEmpty()) {
            return null;
        }
        return prosLoaded.get(s);
    }

    public static int getDefaultExpToLevelUp() {
        return defaultExpToLevelUp;
    }

    public static int getMaxLevel() {
        return maxLevel;
    }

    public int getPlayerCurrentProLevel(Player p) {
        if (p == null || !p.isOnline()) {
            return -1;
        }
        YamlConfiguration config = getPlayerDataConfig(p);
        String s = playerToProMap.get(p);
        TPlayerPro pro = prosLoaded.get(s);
        if (pro == null) {
            return -1;
        }
        return Integer.parseInt(config.getString("pros." + pro.getProID()).split("_")[0]);
    }

    public int getPlayerCurrentProExp(Player p) {
        if (p == null || !p.isOnline()) {
            return -1;
        }
        YamlConfiguration config = getPlayerDataConfig(p);
        String s = playerToProMap.get(p);
        TPlayerPro pro = prosLoaded.get(s);
        if (pro == null) {
            return -1;
        }
        return Integer.valueOf(config.getString("pros." + pro.getProID()).split("_")[1]);
    }

    private static TPVP TPVPInstance;

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdir();
        }

        config = getConfig();
        LoadPros();

        playerListener = new PlayerListener(this);
        this.getServer().getPluginManager().registerEvents(playerListener, this);
        tProGUIListener = new TProGUI(this);
        this.getServer().getPluginManager().registerEvents(tProGUIListener, this);
        getServer().getPluginCommand("tpro").setExecutor(new TProCommandExecutor(this));

        getLogger().info("Enabled TPro v1.0 author: Tracer");


        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            TProExpansion ex = new TProExpansion(this);
            ex.register();
        }
        if (getServer().getPluginManager().getPlugin("TPVP") != null) {
            TPVPInstance = TPVP.getInstance();
            getLogger().info("TPRO 已关联到 TPVP");
        }

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                for (Player p : Bukkit.getOnlinePlayers()) {
//
//                    String itemString = TProCommandExecutor.getItemString(p.getInventory().getItemInMainHand());
//                    String proID = itemToProMap.get(itemString), oldProId = playerToProMap.get(p);
//                    TPlayerPro pro = null, oldPro = null;
//                    if (proID != null) {
//                        pro = prosLoaded.get(proID);
//                    }
//                    if (oldProId != null) {
//                        oldPro = prosLoaded.get(oldProId);
//                    }
//
//                    if (oldPro == pro || pro == null) {
//                        continue;
//                    }
//
//                    boolean inBattle = TPVP.isInBattle(p);
//
//                    if (inBattle) {
//                        TPVP.msg(p, "&7&l您当前处于战斗状态中!无法进行职业切换!");
//                        continue;
//                    }
//
//                    p.sendMessage("您已成功从【" + (oldPro == null ? "N/A" : oldPro.getDisplayName()) + "】切换为【" + pro.getDisplayName() + "】");
//                    playerToProMap.put(p, pro.getProID());
//
//
//                }
//            }
//        }.runTaskTimer(this, 1L, 1L);

        for (Player p : getServer().getOnlinePlayers()) {
            changePro(p, p.getInventory().getItemInMainHand());
        }
    }

    public void LoadPros() {

        prosLoaded.clear();
        this.defaultExpToLevelUp = config.getInt("default.expTpLevelUp");
        this.maxLevel = config.getInt("default.maxLevel");
        ConfigurationSection prosSec = config.getConfigurationSection("pros");
        for (String rootProID : prosSec.getKeys(false)) {
            LoadPro("pros." + rootProID, null);
        }

        ConfigurationSection display = getConfig().getConfigurationSection("display");
        if (display != null) {
            for (String k : display.getKeys(false)) {
                prosLoaded.get(k).setDisplayName(display.getString(k));
                getLogger().info(k + " set display name " + display.getString(k));
            }
        }


        for (Map.Entry<String, TPlayerPro> e : prosLoaded.entrySet()) {
            getLogger().info("PRO " + e.getKey() + " has been Loaded");
            TPlayerPro p = e.getValue();
            //p.show();
        }

        getLogger().info("TOTALLY LOADED PRO " + prosLoaded.entrySet().size());

        getLogger().info(itemToProMap.entrySet().toString());

    }

    /**
     * allow the action happen
     *
     * @param p
     * @return
     */
    public boolean changePro(Player p, ItemStack item) {

        String itemString = TProCommandExecutor.getItemString(item);
        String proID = itemToProMap.get(itemString), oldProId = playerToProMap.get(p);
        TPlayerPro pro = null, oldPro = null;
        if (proID != null) {
            pro = prosLoaded.get(proID);
        }
        if (oldProId != null) {
            oldPro = prosLoaded.get(oldProId);
        }

        if (oldPro == pro || pro == null) {
            return true;
        }

        boolean inBattle = TPVP.isInBattle(p);

        if (inBattle) {
            TPVP.msg(p, "&7&l您当前处于战斗状态中!无法进行职业切换!");
            return false;
        }

        if (oldPro != null) {
            TPVP.msg(p,"&7&l您已成功从【"+ oldPro.getDisplayName() + "&r&7&l】切换为【" + pro.getDisplayName() + "&r&7&l】");
        } else {
            TPVP.msg(p,"&7&l您的当前职业为【"+ pro.getDisplayName() + "&r&7&l】");
        }


        playerToProMap.put(p, pro.getProID());

        return true;

    }

    public void giveExpToPro(Player p, int expToGive, String pro) {
        TPlayerPro currentProOfPlayer = prosLoaded.get(pro);
        if (!currentProOfPlayer.isRootPro() ||
                (currentProOfPlayer.getParentProID() != null && !currentProOfPlayer.getParentProID().equals(""))) {
            TPlayerPro parentPro = currentProOfPlayer.getParentPro();
            getLogger().info("pro " + pro + " is not root pro, going level up pro of " + parentPro.getProID());
            giveExpToPro(p, expToGive, parentPro.getProID());
        }
        YamlConfiguration config = getPlayerDataConfig(p);
        String[] s = config.getString("pros." + pro).split("_");
        int currentLevel = Integer.parseInt(s[0]);
        int currentExp = Integer.parseInt(s[1]);

        currentExp += expToGive;


        int expNextLevel = getConfig().getInt(currentProOfPlayer.getAbsolutePath() + ".levels." + (currentLevel + 1) + ".exp", -1);
        if (expNextLevel >= 0) {
            while (currentExp >= expNextLevel) {
//                getLogger().info("CURRENT LEVEL=" + currentLevel);
//                getLogger().info("CURRENT EXP=" + currentExp);
//                getLogger().info("EXP NEXT LEVEL=" + expNextLevel);

                //level up
                currentLevel += 1;
                if (!giveAwardToCurrentPro(p, currentLevel)) {
                    getLogger().info("CURRENT PRO NOT FOUND OF PLAYER " + p.getName());
                }

                expNextLevel = getConfig().getInt(currentProOfPlayer.getAbsolutePath() + ".levels." + (currentLevel + 1) + ".exp", -1);

                if (expNextLevel < 0) {
                    break;
                }

            }
        }

        config.set("pros." + pro, currentLevel + "_" + currentExp);
        try {
            config.save(getPlayerDataFile(p));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean giveAwardToCurrentPro(Player p, int level) {
        String proId = playerToProMap.get(p);
        if (proId == null || proId.equals("")) {
            return false;
        }
        return giveAwardToThePro(p, level, prosLoaded.get(proId));
    }

    public boolean giveAwardToThePro(Player p, int level, TPlayerPro tPro) {
        if (tPro == null || p == null || !p.isOnline()) {
            return false;
        }
        List<String> award = getConfig().getStringList(tPro.getAbsolutePath() + ".levels." + level + ".award");
        if (award == null) {
            getLogger().info("LEVEL " + level + " FOR PRO " + tPro.getProID() + " HAS NOT SET AWARD LIST!");
            return true;
        }
        award.replaceAll(new UnaryOperator<String>() {
            @Override
            public String apply(String s) {
                return s.replaceAll("%player%", p.getName());
            }
        });
        for (String cmd : award) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            getLogger().info("EXECUTE COMMAND OF AWARD OF PLAYER " + p.getName() + ": " + cmd);
        }


        return false;
    }

    public void giveExpToCurrentPro(Player p, int exp) {
        String s = playerToProMap.get(p);
        if (s == null)
            return;
        TPlayerPro pro = prosLoaded.get(s);
        if (pro == null)
            return;
        giveExpToPro(p, exp, pro.getProID());
        p.sendMessage("§e【" + pro.getDisplayName() + "】获得经验【" + exp + "】");
    }

    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("DISABLED");
    }

    public TPlayerPro LoadPro(String proPath, TPlayerPro parentPro) {

        HashMap<Integer, List<String>> levelsAward = new HashMap<Integer, List<String>>();
        HashMap<Integer, Integer> levelsExpNeed = new HashMap<Integer, Integer>();

        ConfigurationSection thisProSec = config.getConfigurationSection(proPath);
        if (thisProSec == null)
            return null;


        ConfigurationSection levelSec = thisProSec.getConfigurationSection("levels");

        if (levelSec != null) {
            for (String levelStr : levelSec.getKeys(false)) {
                Integer level = null;
                try {
                    level = Integer.valueOf(levelStr);
                } catch (NumberFormatException e) {
                    getLogger().warning("Invalid Number of Level!! +++ERROR+++");
                } finally {
                    if (level == null) {
                        continue;
                    }
                }

                int exp = levelSec.getConfigurationSection(levelStr).getInt("exp");
                List<String> award = levelSec.getConfigurationSection(levelStr).getStringList("award");

                levelsAward.put(level, award);
                levelsExpNeed.put(level, exp);

            }
        }
        TPlayerPro pro = new TPlayerPro(thisProSec.getName(), parentPro, levelsAward, levelsExpNeed);


        List<String> items = thisProSec.getStringList("item");
        pro.items.clear();
        pro.items.addAll(items);


        LinkedList<TPlayerPro> subPros = new LinkedList<>();
        ConfigurationSection subSec = thisProSec.getConfigurationSection("sub");
        if (subSec != null) {
            if (!subSec.getKeys(false).isEmpty()) {
                for (String subSecKey : subSec.getKeys(false)) {

                    subPros.add(LoadPro(proPath + ".sub." + subSecKey, pro));
                }
            } else {
                getLogger().info("Pro " + thisProSec.getName() + " has no more sub pro");
            }
        }
        for (TPlayerPro subPro : subPros) {
            pro.AddSubPro(subPro);
        }

        ConfigurationSection skillSec = thisProSec.getConfigurationSection("skills");
        if (skillSec != null) {
            for (String skillKey : skillSec.getKeys(false)) {
                String skillCommand = skillSec.getString(skillKey);
                pro.AddSkill(skillKey, skillCommand);
            }
        }
        pro.setAbsolutePath(proPath);
        prosLoaded.put(pro.getProID(), pro);


        for (String item : items) {
            itemToProMap.put(item, pro.getProID());
        }


        return pro;
    }


    public Inventory getInspectGUI() {
        return tProGUIListener.getGUI();
    }
}
