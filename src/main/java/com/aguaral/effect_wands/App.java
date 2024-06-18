package com.aguaral.effect_wands;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class App extends JavaPlugin implements Listener {
    private Gson gson;
    // if you really want to optimize, you can try making this a hashmap, but i'm lazy
    private List<Wand> wandList;
    private final String dataPath = "plugins\\effect_wands\\wands.json";

    // i have tried every kind of potion enumerator i could think of and then gave up
    public static final HashMap<String, PotionEffectType> nameToEffect = new HashMap<String, PotionEffectType>() {{
        put("Speed", PotionEffectType.SPEED);
        put("Haste", PotionEffectType.FAST_DIGGING);
        put("Strength", PotionEffectType.INCREASE_DAMAGE);
        put("JumpBoost", PotionEffectType.JUMP);
        put("Regeneration", PotionEffectType.REGENERATION);
        put("Resistance", PotionEffectType.DAMAGE_RESISTANCE);
        put("FireResistance", PotionEffectType.FIRE_RESISTANCE);
        put("WaterBreathing", PotionEffectType.WATER_BREATHING);
        put("Invisibility", PotionEffectType.INVISIBILITY);
        put("NightVision", PotionEffectType.NIGHT_VISION);
        put("Saturation", PotionEffectType.SATURATION);
        put("HealthBoost", PotionEffectType.HEALTH_BOOST);
        put("Luck", PotionEffectType.LUCK);
        put("Dolphin", PotionEffectType.DOLPHINS_GRACE);
        put("Conduit", PotionEffectType.CONDUIT_POWER);
        put("Hero", PotionEffectType.HERO_OF_THE_VILLAGE);
        // put("Bad Omen", PotionEffectType.BAD_OMEN);
    }};

    @Override
    public void onEnable() {
        this.getCommand("createtomes").setExecutor(new WandCreator());
        this.getCommand("resettome").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        gson = new Gson();
        
        setupWandList();
        startScheduler();
    }

    // command "resettome" resets the writings in the held tome
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            PlayerInventory playerInventory = player.getInventory();
            ItemStack heldItemStack = playerInventory.getItem(playerInventory.getHeldItemSlot());
            
            // verify we are holding a tome
            if(heldItemStack.getType() == Material.WRITABLE_BOOK &&
                heldItemStack.containsEnchantment(Enchantment.ARROW_INFINITE))
            {
                // reset the pages
                BookMeta bookMeta = (BookMeta) heldItemStack.getItemMeta();
                bookMeta.setPages(new ArrayList<String>());
                heldItemStack.setItemMeta(bookMeta);
                this.onEvent(new PlayerEditBookEvent(player, playerInventory.getHeldItemSlot(), bookMeta, bookMeta, false));
            }
        }
        return true;
    }

    // precondition: server startup
    // postcondition: wandlist has a value
    private void setupWandList()
    {
        /**
         * initialize wandlist
         */
        // new directory: set up the wands to start
        if(getDataFolder().mkdirs())
        {
            // first startup -- we need to set up the wand files!
            wandList = new ArrayList<>();
            for(String effectName : nameToEffect.keySet())
                wandList.add(new Wand(effectName));
        }
        else
        {
            // every non-first startup -- deserialize the list
            try(FileReader reader = new FileReader(dataPath))
            {
                List<LinkedTreeMap> rawRead = new ArrayList<LinkedTreeMap>();
                rawRead = gson.fromJson(reader, rawRead.getClass());
                // getLogger().info("Deserialized: " + rawRead.toString());
                // getLogger().info("Deserialized type: " + rawRead.getClass().getName());
                // getLogger().info("Deserialized inner type: " + rawRead.get(0).getClass().getName());
                
                // convert the received json to the correct Wand type using an inner deserialize
                wandList = new ArrayList<Wand>();
                for(LinkedTreeMap linkedTreeMap : rawRead)
                {
                    // getLogger().info(linkedTreeMap.toString());
                    wandList.add(gson.fromJson(linkedTreeMap.toString(), Wand.class));
                }
                // getLogger().info("wandList: " + wandList);
            }
            catch(FileNotFoundException e)
            {
                getLogger().info("Data file not found!");
            }
            catch(IOException e)
            {
                getLogger().info("Unknown IOException!");
            }
        }
    }

    // precondition: wandlist is set up
    // postcondition: repeating effect every 10s applies to players
    private void startScheduler()
    {
        // begin scheduler for applying potion effects
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                applyEffects();
            }
        }, 0L, 200L);
    }

    /**
     * save to json
     */
    @Override
    public void onDisable() {
        getLogger().info(dataPath);

        try(FileWriter writer = new FileWriter(dataPath))
        {
            gson.toJson(wandList, writer);
        }
        catch(IOException e)
        {
            getLogger().info("Unknown IOException!");
        }
    }    

    private void applyEffects()
    {
        for(Wand wand : wandList)
            wand.applyToPlayers();
    }

    private Wand getWandByEffect(String effect)
    {
        // getLogger().info("Searching for wand " + effect);
        for(Wand wand : wandList)
        {
            // getLogger().info("Found wand " + wand.getEffectName());
            if(wand.getEffectName().equals(effect)) return wand;
        }
            
        return null;
    }

    @EventHandler
    public void onEvent(PlayerEditBookEvent bookEvent)
    {
        BookMeta bookMeta = bookEvent.getNewBookMeta();
        if(!bookMeta.hasEnchant(Enchantment.ARROW_INFINITE)) return;
        bookMeta.getLore();
        Wand wandToUse = getWandByEffect(bookMeta.getLore().get(0).substring(WandCreator.loreText.length() - 2));
        List<String> collectedText = new ArrayList<>();
        for(String page : bookMeta.getPages())
            collectedText.addAll(Arrays.asList(page.split("\\r?\\n")));
        // getLogger().info("Player list for wand " + wandToUse.getEffectName() + ": " + collectedText.toString());
        wandToUse.setPlayers(collectedText);
    }
}