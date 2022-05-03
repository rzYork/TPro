package com.tracer.Expansion;

import com.tracer.Entity.TPlayerPro;
import com.tracer.TPro;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TProExpansion extends PlaceholderExpansion {
    private TPro plugin;

    public TProExpansion(TPro plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull
    String getIdentifier() {
        return "tpro";
    }

    @Override
    public @NotNull
    String getAuthor() {
        return "tracer";
    }

    @Override
    public @NotNull
    String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable
    String onRequest(OfflinePlayer player, @NotNull String params) {
        if (!player.isOnline()) {
            return null;
        }
        Player p = (Player) player;
        String currentProId = TPro.playerToProMap.get(p);
        if (currentProId == null || currentProId.equalsIgnoreCase("")) {
            return "N/A";
        }
        TPlayerPro playerPro = plugin.getPlayerPro(p);

        if (params.equalsIgnoreCase("current_pro")) {
            return playerPro.getDisplayName();
        } else if (params.equalsIgnoreCase("current_exp")) {
            String s = plugin.getPlayerDataConfig(p).getString("pros." + currentProId);
            String[] split = s.split("_");
            int exp = -1;
            try {
                exp = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("CONFIGURATION FILE FORMAT ERROR_+++++_");
                e.printStackTrace();
            }
            return exp < 0 ? "N/A" : exp + "";
        } else if (params.equalsIgnoreCase("exp_to_next")) {
            TPlayerPro currentPro = plugin.prosLoaded.get(currentProId);
            if (currentPro == null) {
                return "N/A";
            }
            String s = plugin.getPlayerDataConfig(p).getString("pros." + currentProId);
            String[] split = s.split("_");
            int level = Integer.valueOf(split[0]);
            int exp = Integer.valueOf((split[1]));
            Integer toNextLevel = currentPro.getExpNeedOfLevel(level + 1) - exp;
            return toNextLevel.toString();
        } else if (params.equals("current_pro_level")) {
            int playerCurrentProLevel = plugin.getPlayerCurrentProLevel(p);
            return playerCurrentProLevel < 0 ? "N/A" : playerCurrentProLevel + "";
        }
        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

}
