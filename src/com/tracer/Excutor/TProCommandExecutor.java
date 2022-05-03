package com.tracer.Excutor;

import com.tracer.Entity.TPlayerPro;
import com.tracer.TPro;
import github.saukiya.sxattribute.data.attribute.sub.attack.Damage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TProCommandExecutor implements CommandExecutor {
    private TPro plugin;

    public TProCommandExecutor(TPro plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (s.equalsIgnoreCase("tpro")) {
            if (args.length < 1) {
                return false;
            }
            if (args.length == 1) {if(args[0].equalsIgnoreCase("unbind")){
                if(!sender.hasPermission("tpro.unbind")){
                    sender.sendMessage("You have no permission to use this!");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command is only available for Player");
                    return true;
                }
                Player p = (Player) sender;

                ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
                if (itemInMainHand.getType() == Material.AIR) {
                    p.sendMessage("你不可以设置空气");
                    return true;
                }
                String itemStr=getItemString(itemInMainHand);
                String proStrToUnbind = TPro.itemToProMap.get(itemStr);
                if(proStrToUnbind==null||proStrToUnbind.isEmpty()||plugin.prosLoaded.get(proStrToUnbind)==null){
                    p.sendMessage("此物品未绑定到任何职业");
                    return true;
                }
                TPlayerPro proToUnbind = plugin.prosLoaded.get(proStrToUnbind);
                String displayName = proToUnbind.getDisplayName();
                proToUnbind.items.remove(itemStr);
                TPro.itemToProMap.remove(itemStr);
                plugin.getConfig().set(proToUnbind.absolutePath+".item",proToUnbind.items);
                plugin.saveConfig();
                p.sendMessage("当前物品已从职业"+displayName+"解绑");
                return true;

            }
            else
            if (args[0].equals("help")) {
                    if(!sender.hasPermission("tpro.use")){
                        sender.sendMessage("You have no permission to use this!");
                        return true;
                    }
                    sender.sendMessage("/TPro skill <skillName> RELEASE THE SKILL OF CURRENT PRO");
                    sender.sendMessage("/TPro exp <player>    SEEK THE EXP OF PLAYER/OR SELF");
                    sender.sendMessage("/TPro bind <proName>    BIND THE ITEM FOR SPECIAL PRO -- OP ONLY");
                    sender.sendMessage("/TPro pro <player>    SEEK THE PRO OF PLAYER/OR SELF");
                    sender.sendMessage("/TPro giveexp <player> exp GIVE THE PLAYER SPECIAL EXP AND LEVEL UP TO ALL ITS PARENT PRO");
                    sender.sendMessage("/TPro see             INSPECT ALL DATA LOADED");
                    sender.sendMessage("/TPro unbind          UNBIND THE ITEM FOR THE PRO --OP ONLY");
                    return true;
                }else if (args[0].equalsIgnoreCase("bind")) {
                    if(!sender.hasPermission("tpro.bind")){
                        sender.sendMessage("You have no permission to use this!");
                        return true;
                    }
                    sender.sendMessage("/TPro bi <proName>    BIND THE ITEM FOR SPECIAL PRO -- OP ONLY");
                    return true;
                }else if(args[0].equalsIgnoreCase("reload")){
                    if(!sender.hasPermission("tpro.reload")){
                        sender.sendMessage("You have no permission to use this!");
                        return true;
                    }
                    plugin.reloadConfig();
                    plugin.LoadPros();
                    sender.sendMessage("Plugin is successfully reloaded!");
                    return true;
                }else if(args[0].equalsIgnoreCase("see")){
                    if(!sender.hasPermission("tpro.see")){
                        sender.sendMessage("You have no permission to use this!");
                        return true;
                    }
                    if (!(sender instanceof Player)) {

                        sender.sendMessage("This action is only available for the Player!");
                        return true;
                    }
                    Player p=(Player)sender;
                    p.openInventory(plugin.getInspectGUI());
                }
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("bind")) {
                    if(!sender.hasPermission("tpro.bind")){
                        sender.sendMessage("You have no permission to use this!");
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("This command is only available for Player");
                        return true;
                    }
                    Player p = (Player) sender;
                    String proName = args[1];
                    TPlayerPro tPlayerPro = plugin.prosLoaded.get(proName);
                    if (tPlayerPro == null) {
                        sender.sendMessage("PRO " + proName + " is not found!");
                        return true;
                    }
                    ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
                    if (itemInMainHand.getType() == Material.AIR) {
                        p.sendMessage("YOU COULD NOT SET THE AIR !");
                        return true;
                    }
                    String itemStr=getItemString(itemInMainHand);
                    boolean set=false;
                    String proSetDisplayName = "";

                    if(TPro.itemToProMap.get(itemStr)!=null){
                            set=true;
                            proSetDisplayName=plugin.prosLoaded.get(TPro.itemToProMap.get(itemStr)).getDisplayName();}
                    if(set){
                        sender.sendMessage("这个物品已设定为职业: "+proSetDisplayName);
                        return true;
                    }

                    tPlayerPro.items.add(itemStr);
                    TPro.itemToProMap.put(itemStr, tPlayerPro.getProID());
                    plugin.getConfig().set(tPlayerPro.getAbsolutePath() + ".item",tPlayerPro.items);
                    plugin.saveConfig();
                    plugin.LoadPro(tPlayerPro.getAbsolutePath(),tPlayerPro.getParentPro());


                    p.sendMessage("成功设置为职业 [" + tPlayerPro.getDisplayName() + "]");


                    return true;
                } else if (args[0].equalsIgnoreCase("exp")) {
                    if(!sender.hasPermission("tpro.exp")){
                        sender.sendMessage("You have no permission to use this!");
                        return true;
                    }
                    Player targetP = plugin.getServer().getPlayer(args[1]);
                    if (targetP == null || !targetP.isOnline()) {
                        sender.sendMessage("Target player does not exist or not online!");
                        return true;
                    }
                    sender.sendMessage("Player " + targetP.getName() + " Current Pro Exp " + plugin.getPlayerCurrentProExp(targetP));
                    return true;
                } else if (args[0].equalsIgnoreCase("pro")) {
                    if(!sender.hasPermission("tpro.pro")){
                        sender.sendMessage("You have no permission to use this!");
                        return true;
                    }
                    Player targetP = plugin.getServer().getPlayer(args[1]);
                    if (targetP == null || !targetP.isOnline()) {
                        sender.sendMessage("Target player does not exist or not online!");
                        return true;
                    }
                    sender.sendMessage("Player " + targetP.getName() + " Current Pro  " + TPro.playerToProMap.get(targetP));
                    return true;
                } else if(args[0].equalsIgnoreCase("skill")){
                    String skillName=args[1];
                    if(!(sender instanceof Player)){
                        sender.sendMessage("This command is only available for the Player!");
                        return true;
                    }
                    Player p=(Player)sender;
                    TPlayerPro pro = plugin.getPlayerPro(p);
                    if(pro==null){
                        p.sendMessage("Your current pro is invalid!");
                        return true;
                    }
                    String skillCommand = pro.getSkill(skillName);
                    if(skillCommand==null||skillCommand.isEmpty()){
                        p.sendMessage("The skill for your current pro is not available");
                        return true;
                    }
                    Bukkit.dispatchCommand(p,skillCommand);
                    p.sendMessage("You used skill: "+skillName);
                    return true;
                }
            } else if (args.length == 3) {
                if(!sender.hasPermission("tpro.giveexp")){
                    sender.sendMessage("You have no permission to use this!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("giveexp")) {
                    Player targetPlayer = plugin.getServer().getPlayer(args[1]);
                    if (targetPlayer == null || !targetPlayer.isOnline()) {
                        sender.sendMessage("Target Player does not exists or not online!");
                        return true;
                    }
                    @NotNull
                    Integer exp;
                    try {
                        exp = Integer.valueOf(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("EXP SHOULD BE AN INTEGER!");
                        return true;
                    }
                    String s1 = TPro.playerToProMap.get(targetPlayer);
                    if (s1 == null || s1.equals("")) {
                        sender.sendMessage("TARGET PLAYER HAS NO PROFESSIONAL");
                        return true;
                    }
                    plugin.giveExpToCurrentPro(targetPlayer, exp);
                    return true;

                }
                return false;
            }
        }
        return false;
    }
    public static String getItemString(ItemStack stack){
        if(stack==null)
            return "";
        ItemStack clone = stack.clone();
        clone.setAmount(1);
        ItemMeta im1=Bukkit.getItemFactory().getItemMeta(stack.getType());
        if(clone.hasItemMeta()){
            im1=clone.getItemMeta();
        }
        if(im1 instanceof Damageable){
            ((Damageable)im1).setDamage(0);
        }
        clone.setItemMeta(im1);
        return clone.serialize().toString();
    }
}
