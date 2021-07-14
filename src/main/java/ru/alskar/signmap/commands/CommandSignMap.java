package ru.alskar.signmap.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.alskar.signmap.types.PersistentUUID;
import ru.alskar.signmap.SignMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CommandSignMap extends BaseCommand {

    private static final SignMap plugin = SignMap.getInstance();

    @CommandAlias("signmap")
    public static void signMap(Player player) {
        // If player doesn't hold a map, show them an error message and do nothing.
        if (player.getInventory().getItemInMainHand().getType() != Material.FILLED_MAP) {
            player.sendMessage(plugin.getLocale().ERROR_NO_MAP_IN_HAND_TO_SIGN);
            return;
        }

        // Otherwise, let's look closer at the map!
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        // Has it already been signed by anyone?
        if (container.has(plugin.getKeyUUID(), new PersistentUUID())) {
            if (container.has(plugin.getKeyName(), PersistentDataType.STRING)) {
                String authorName = container.get(plugin.getKeyName(), PersistentDataType.STRING);
                player.sendMessage(String.format(plugin.getLocale().ERROR_MAP_ALREADY_SIGNED, authorName));
            } else {
                player.sendMessage(String.format(plugin.getLocale().ERROR_MAP_ALREADY_SIGNED,
                        plugin.getLocale().UNKNOWN_PLAYER));
            }
            return;
        }

        // No? Let's sign it then!
        // Writing player's UUID into persistent container of an item:
        UUID uuid = player.getUniqueId();
        container.set(plugin.getKeyUUID(), new PersistentUUID(), uuid);
        // Also, we write player's name:
        // Not to forget to reset Chat Color, so we don't mess this up when it's time to /unsign
        String authorName = ChatColor.stripColor(player.getDisplayName());
        container.set(plugin.getKeyName(), PersistentDataType.STRING, authorName);
        // And the text which we'll put in item's lore:
        String loreText = String.format(plugin.getLocale().LORE_TEXT, authorName);
        container.set(plugin.getKeyLore(), PersistentDataType.STRING, loreText);
        // We also add lore line saying who signed the map:
        List<String> lore = ((lore = itemMeta.getLore()) != null) ? lore : new ArrayList<>();
        lore.add(loreText);
        itemMeta.setLore(lore);
        // Saving changes:
        item.setItemMeta(itemMeta);
        player.sendMessage(String.format(plugin.getLocale().SUCCESSFULLY_SIGNED, authorName));
    }

    @CommandAlias("unsignmap")
    public static void unsignMap(Player player) {
        // If player doesn't hold a map, show them an error message and do nothing.
        if (player.getInventory().getItemInMainHand().getType() != Material.FILLED_MAP) {
            player.sendMessage(plugin.getLocale().ERROR_NO_MAP_IN_HAND_TO_UNSIGN);
            return;
        }

        // Otherwise, let's look closer at the map!
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        // Has it already been signed by anyone? If not, print a message and relax.
        if (!container.has(plugin.getKeyUUID(), new PersistentUUID())) {
            player.sendMessage(plugin.getLocale().ERROR_MAP_NOT_SIGNED);
            return;
        }

        // Otherwise, we check if the player is allowed to unsign the map. If not, print a message and forget.
        UUID uuid = player.getUniqueId();
        UUID authorUUID = container.get(plugin.getKeyUUID(), new PersistentUUID());
        if (!uuid.equals(authorUUID) && !player.hasPermission("signmap.unsign.others")) {
            player.sendMessage(plugin.getLocale().ERROR_NOT_ALLOWED_TO_UNSIGN);
            return;
        }

        // If player is allowed, we start with removing data from persistent container.
        container.remove(plugin.getKeyUUID());
        if (container.has(plugin.getKeyName(), PersistentDataType.STRING)) {
            container.remove(plugin.getKeyName());
        }
        // If there's lore text saved in persistent container, save it before removing data.
        String loreText = null;
        if (container.has(plugin.getKeyLore(), PersistentDataType.STRING)) {
            loreText = container.get(plugin.getKeyLore(), PersistentDataType.STRING);
            container.remove(plugin.getKeyLore());
        }
        // If item was signed before locale.yml and 'lore-text' key were introduced, also look for an outdated text:
        String outdatedLoreText = "§7Signed by §6";
        // And, finally, let's remove our custom lore from the item:
        List<String> lore = itemMeta.getLore();
        if (lore != null) {
            // Had to drop that beautiful one-line lambda because loreText isn't final D:
            // I waaaant to sleep today', not to forget to look at this later.
            Iterator<String> line = lore.iterator();
            String lineText;
            while (line.hasNext()) {
                // If item has no lore text in its persistent container, look only for an outdated text:
                lineText = line.next();
                if ((loreText == null)
                        ? lineText.contains(outdatedLoreText)
                        : lineText.equals(loreText) || lineText.contains(outdatedLoreText)) {
                    line.remove();
                }
            }
            itemMeta.setLore(lore);
        }
        // Saving changes:
        item.setItemMeta(itemMeta);

        player.sendMessage(plugin.getLocale().SUCCESSFULLY_UNSIGNED);
    }
}
