package com.tracer.Entity;

import com.tracer.TPro;
import io.lumine.xikage.mythicmobs.utils.config.properties.types.StringListProp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class TPlayerPro {

    public String proID;
    public TPlayerPro parentPro;
    public HashMap<Integer, List<String>> levelsAward;
    public HashMap<Integer, Integer> levelsExpNeed;
    public List<TPlayerPro> subPros;
    public HashMap<String, String> skillMap;
    public List<String> items=new ArrayList<>();



    public String getParentProID() {
        return parentProID;
    }

    public String absolutePath;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String parentProID;


    public String displayName;

    public String getDisplayName() {
        return (displayName == null || displayName.isEmpty()) ? proID : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName+="&l";
        this.displayName=ChatColor.translateAlternateColorCodes('&',displayName);
    }

    public boolean isRootPro() {
        return isRootPro;
    }

    @Override
    public String toString() {
        return "TPlayerPro{" +
                "proID='" + proID + '\'' +
                ", parentPro=" + parentPro +
                ", levelsAward=" + levelsAward +
                ", levelsExpNeed=" + levelsExpNeed +
                ", subPros=" + subPros +
                ", parentProID='" + parentProID + '\'' +
                ", isRootPro=" + isRootPro +
                '}';
    }

    public String getProID() {
        return proID;
    }

    public TPlayerPro getParentPro() {
        return parentPro;
    }

    private boolean isRootPro;

    public int getExpNeedOfLevel(int level) {
        Integer integer = levelsExpNeed.get(level);
        if (integer == null) {
            return TPro.getDefaultExpToLevelUp();
        }
        return integer;

    }

    public List<String> getAwardOfLevel(int level) {
        return new ArrayList<>(getAwardOfLevel(level));
    }

    public TPlayerPro(String proID, TPlayerPro parentPro, HashMap<Integer, List<String>> levelsAward, HashMap<Integer, Integer> levelsExpNeed) {
        this.subPros = new LinkedList<>();
        this.skillMap = new HashMap<>();

        this.proID = proID;
        this.parentPro = parentPro;
        this.levelsAward = levelsAward;
        this.levelsExpNeed = levelsExpNeed;

        this.parentProID = parentPro == null ? "" : parentPro.getProID();

        this.isRootPro = (parentPro == null || parentPro.equals(""));
    }

    public void AddSubPro(TPlayerPro pro) {
        this.subPros.add(pro);
    }

    public Iterator<TPlayerPro> getSubProIterator() {
        return subPros.iterator();
    }

    public void AddSkill(String skillName, String skillCommand) {
        this.skillMap.put(skillName, skillCommand);
    }

    public HashMap<Integer, List<String>> getLevelsAward() {
        return levelsAward;
    }

    public HashMap<Integer, Integer> getLevelsExpNeed() {
        return levelsExpNeed;
    }

    public String getSkill(String skillName) {
        return this.skillMap.get(skillName);
    }

    public void show() {
        Bukkit.getLogger().info("PRO: " + proID);
        Bukkit.getLogger().info("PARENT: " + parentProID);
        for (Map.Entry<Integer, List<String>> e : levelsAward.entrySet()) {
            Bukkit.getLogger().info("LEVEL_" + e.getKey() + " with AWARDS: ");
            for (String s : e.getValue()) {
                Bukkit.getLogger().info(s);
            }
        }
        for (Map.Entry<Integer, Integer> e : levelsExpNeed.entrySet()) {
            Bukkit.getLogger().info("LEVEL_" + e.getKey() + " need EXP: " + e.getValue());
        }
        Bukkit.getLogger().info("isRootPro: " + isRootPro);
        Bukkit.getLogger().info("SUB PROS COUNT:" + subPros.size());
    }

    public HashMap<String, String> getSkillMap() {
        return skillMap;
    }
}
