package com.aguaral.effect_wands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class WandCreator implements CommandExecutor {
    public static String loreText = ChatColor.RESET + "" + ChatColor.RED + ChatColor.BOLD;

    @Override
    // gives the player all wands
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            giveWandsToPlayer(player);
        }
        return false;
    }

    private void giveWandsToPlayer(Player player)
    {
        PlayerInventory playerInventory = player.getInventory();

        for(String effect : App.nameToEffect.keySet())
        {
            ItemStack wand = new ItemStack(Material.NETHER_STAR);
            wand.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
            wand.setAmount(1);
            ItemMeta wandMeta = wand.getItemMeta();

            // set up metadata of wand
            wandMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.BLUE + "Artifact of " + ChatColor.RED + ChatColor.BOLD + effect);
            wandMeta.setUnbreakable(true);
            
            List<String> lore = new ArrayList<String>();
            lore.add(loreText + effect);
            wandMeta.setLore(lore);

            // add metadata & give
            wand.setItemMeta(wandMeta);
            playerInventory.addItem(wand);
        }
    }
}
