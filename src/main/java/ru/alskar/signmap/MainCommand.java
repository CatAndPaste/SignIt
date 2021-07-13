package ru.alskar.signmap;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainCommand extends BaseCommand {

    private static final SignMap plugin = SignMap.getInstance();

    @CommandAlias("signmap")
    public static void signMap(Player player, String[] args) {
        if (player.getInventory().getItemInMainHand().getType() != Material.FILLED_MAP) {
            player.sendMessage("§cYou need to have the map you want to sign in your main hand!");
            return;
        }

        // Let's now look closer at the map in player's hand!
        ItemStack map = player.getInventory().getItemInMainHand();
        ItemMeta mapMeta = map.getItemMeta();
        PersistentDataContainer container = mapMeta.getPersistentDataContainer();
        // Has it already been signed by anyone?
        if (container.has(plugin.getKeyUUID(), new PersistentUUID())) {
            if (container.has(plugin.getKeyName(), PersistentDataType.STRING)) {
                String authorName = container.get(plugin.getKeyName(), PersistentDataType.STRING);
                player.sendMessage(String.format("§cMap is already signed by %s.", authorName));
            } else {
                player.sendMessage("§cMap is already signed by unknown player.");
            }
            return;
        }
        // No? Let's sign it then!

        // Writing player's UUID into persistent container of an item:
        UUID uuid = player.getUniqueId();
        container.set(plugin.getKeyUUID(), new PersistentUUID(), uuid);
        // Also, we write player's name,
        // because I'm a lazy ass to read about how to get the name off UUID, sorry :/
        String authorName = player.getDisplayName();
        container.set(plugin.getKeyName(), PersistentDataType.STRING, authorName);
        // We also add lore line saying who signed the map:
        List<String> lore = ((lore = mapMeta.getLore()) != null) ? lore : new ArrayList<>();
        lore.add("§7Signed by §6" + authorName);
        mapMeta.setLore(lore);
        map.setItemMeta(mapMeta);
        player.sendMessage(String.format("§aMap signed successfully! Author is now set to §e%s§a.", authorName));
    }

    @CommandAlias("unsignmap")
    public static void unsignMap(Player player, String[] args) {
        if (player.getInventory().getItemInMainHand().getType() != Material.FILLED_MAP) {
            player.sendMessage("§cYou need to have the map you want to unsign in your main hand!");
            return;
        }

        // Let's now look closer at the map!
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        // Has it already been signed by anyone?
        if (!container.has(plugin.getKeyUUID(), new PersistentUUID())) {
            player.sendMessage("§cThis map is not signed!");
            return;
        }

        UUID uuid = player.getUniqueId();
        UUID authorUUID = container.get(plugin.getKeyUUID(), new PersistentUUID());
        if (!uuid.equals(authorUUID) && !player.isOp() && !player.hasPermission("signmap.unsign")) {
            player.sendMessage("§cHey, only player that signed this map or server Operator can unsign it!");
            return;
        }

        // Removing data from persistent container
        container.remove(plugin.getKeyUUID());
        if (container.has(plugin.getKeyName(), PersistentDataType.STRING)) {
            container.remove(plugin.getKeyName());
        }

        // And removing "Signed by" line in lore
        List<String> lore = itemMeta.getLore();
        if (lore != null) {
            lore.removeIf(s -> s.contains("§7Signed by §6"));
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }

        player.sendMessage("§aMap unsigned successfully, anyone can copy it now!");
    }
}
