package com.aguaral.effect_wands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;

// class representing a single wand
// has a potion effect and a list of players it should be assigned to 
public class Wand {
    // the list of playernames this wand is applying to
    private final List<String> playerList;
    // duration of this wand's potion effect
    private final int duration = 260;
    // name of the potion effect's name (see App.java's map)
    private final String potionEffectName;
    // level of the potion effect (configurable in wands.json)
    private int amplifier = 1;

    public Wand(String potionEffectName, int amplifier)
    {  
        this.potionEffectName = potionEffectName;
        this.playerList = new ArrayList<>();
        this.amplifier = amplifier;
    }

    public Wand(String potionEffectName)
    {
        this.potionEffectName = potionEffectName;
        this.playerList = new ArrayList<>();
    }

    public String getEffectName()
    {
        return potionEffectName;
    }

    public void setPlayers(List<String> playerList)
    {
        this.playerList.clear();
        this.playerList.addAll(playerList);
    }

    public void applyToPlayers()
    {
        for(String name : playerList)
        {
            if(Bukkit.getPlayer(name) == null) continue;
            PotionEffect potionEffect = new PotionEffect(App.nameToEffect.get(potionEffectName), duration, amplifier);
            Bukkit.getPlayer(name).addPotionEffect(potionEffect); 
        }
    }
}
