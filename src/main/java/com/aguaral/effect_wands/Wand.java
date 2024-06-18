package com.aguaral.effect_wands;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

// class representing a single wand
// has a potion effect and a list of players it should be assigned to 
public class Wand {
    private final Set<String> playerSet;
    private final int duration = 260;
    private final String potionEffectName;
    private int amplifier = 1;

    public Wand(String potionEffectName, int amplifier)
    {  
        this.potionEffectName = potionEffectName;
        this.playerSet = new HashSet<>();
        this.amplifier = amplifier;
    }

    public Wand(String potionEffectName)
    {
        this.potionEffectName = potionEffectName;
        this.playerSet = new HashSet<>();
    }

    public void addPlayer(String playerName)
    {
        playerSet.add(playerName);
    }

    public void removePlayer(String playerName)
    {
        playerSet.remove(playerName);
    }

    public void clearPlayers()
    {
        playerSet.clear();
    }

    public String getEffectName()
    {
        return potionEffectName;
    }

    public List<String> getPlayers()
    {
        return new ArrayList<String>(playerSet);
    }

    public void applyToPlayers()
    {
        for(String name : playerSet)
        {
            if(Bukkit.getPlayer(name) == null) continue;
            PotionEffect potionEffect = new PotionEffect(App.nameToEffect.get(potionEffectName), duration, amplifier);
            Bukkit.getPlayer(name).addPotionEffect(potionEffect); 
        }
    }
}
