package ru.alskar.signmap.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.alskar.signmap.config.Config;
import ru.alskar.signmap.types.PersistentUUID;
import ru.alskar.signmap.SignMap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandSignMap extends BaseCommand {

    private static final SignMap plugin = SignMap.getInstance();

    @CommandAlias("%signmap")
    @Description("Sings map in main hand, preventing others from copying it")
    @CommandPermission("signmap.sign")
    public static void onSign(Player player) {
        // If player doesn't hold a map, show them an error message and do nothing.
        if (player.getInventory().getItemInMainHand().getType() != Material.FILLED_MAP) {
            player.sendMessage(plugin.getLocale().ERROR_NO_MAP_IN_HAND_TO_SIGN);
            return;
        }

        // Otherwise, let's take a closer look at the map!
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta())
            player.sendMessage("[SignMap] Something went wrong, please contact plugin developer.");
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        // Has it already been signed by anyone?
        if (container.has(plugin.getKeyUUID(), new PersistentUUID())) {
            if (container.has(plugin.getKeyName(), PersistentDataType.STRING)) {
                String authorName = container.get(plugin.getKeyName(), PersistentDataType.STRING);
                player.sendMessage(MessageFormat.format(plugin.getLocale().FORMAT_ERROR_MAP_ALREADY_SIGNED, authorName));
            } else {
                player.sendMessage(MessageFormat.format(plugin.getLocale().FORMAT_ERROR_MAP_ALREADY_SIGNED, plugin.getLocale().PH_UNKNOWN_PLAYER));
            }
            return;
        }

        // No? Let's sign it then!
        // Writing player's UUID into persistent container of an item:
        UUID uuid = player.getUniqueId();
        container.set(plugin.getKeyUUID(), new PersistentUUID(), uuid);
        // Also, we write player's name:
        String authorName;
        if (plugin.getConfig().getBoolean(Config.USE_DISPLAY_NAMES)) {
            if (plugin.getConfig().getBoolean(Config.USE_NAMES_COLORS))
                authorName = player.getDisplayName();
            else
                authorName = ChatColor.stripColor(player.getDisplayName());
        } else
            authorName = player.getName();
        container.set(plugin.getKeyName(), PersistentDataType.STRING, authorName);
        // And the text which we'll put in item's lore:
        String loreText = MessageFormat.format(plugin.getLocale().FORMAT_LORE_TEXT, authorName);
        container.set(plugin.getKeyLore(), PersistentDataType.STRING, ChatColor.stripColor(loreText));
        // We also add lore line saying who signed the map:
        List<String> lore = ((lore = itemMeta.getLore()) != null) ? lore : new ArrayList<>();
        lore.add(loreText);
        itemMeta.setLore(lore);
        // Saving changes:
        item.setItemMeta(itemMeta);
        player.sendMessage(MessageFormat.format(plugin.getLocale().FORMAT_SUCCESSFULLY_SIGNED, authorName));
    }

    @CommandAlias("%unsignmap")
    @Description("Unsigns map in main hand, so anyone can copy it. Available only for player who signed the map and operators")
    @CommandPermission("signmap.unsign")
    public static void onUnsign(Player player) {
        // If player doesn't hold a map, show them an error message and do nothing.
        if (player.getInventory().getItemInMainHand().getType() != Material.FILLED_MAP) {
            player.sendMessage(plugin.getLocale().ERROR_NO_MAP_IN_HAND_TO_UNSIGN);
            return;
        }

        // Otherwise, let's take a closer look!
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta())
            player.sendMessage("[SignMap] Something went wrong, please contact plugin developer.");
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
        List<String> loreToRemove = new ArrayList<>();
        if (container.has(plugin.getKeyLore(), PersistentDataType.STRING)) {
            loreToRemove.add(ChatColor.stripColor(container.get(plugin.getKeyLore(), PersistentDataType.STRING)));
            container.remove(plugin.getKeyLore());
        }
        // If item was signed before locale.yml and 'lore-text' key were introduced, also look for an outdated text:
        loreToRemove.add("Signed by ");
        // And, finally, let's remove custom lore we've added in past from the item:
        List<String> lore = itemMeta.getLore();
        if (lore != null) {
            lore.removeIf(s -> loreToRemove.stream().anyMatch(l -> ChatColor.stripColor(s).startsWith(l)));
            itemMeta.setLore(lore);
        }
        // Saving changes:
        item.setItemMeta(itemMeta);

        player.sendMessage(plugin.getLocale().SUCCESSFULLY_UNSIGNED);
    }

    @CommandAlias("signall")
    @Description("Sings all accessible maps in player's inventory")
    @CommandPermission("signmap.signall")
    public static void onSignall(Player player) {
        // Let's look through player's inventory, and if there are no maps, show an error.
        PlayerInventory inv = player.getInventory();
        if (!inv.contains(Material.FILLED_MAP)) {
            player.sendMessage(plugin.getLocale().ERROR_NO_MAPS_IN_INVENTORY);
            return;
        }

        // Otherwise, let's prepare data needed for signing.
        UUID uuid = player.getUniqueId();
        String authorName;
        if (plugin.getConfig().getBoolean(Config.USE_DISPLAY_NAMES)) {
            if (plugin.getConfig().getBoolean(Config.USE_NAMES_COLORS))
                authorName = player.getDisplayName();
            else
                authorName = ChatColor.stripColor(player.getDisplayName());
        } else
            authorName = player.getName();

        // And iterate over inventory
        int succeeded = 0, failed = 0;
        for (ItemStack item : inv.getContents()) {
            if (item == null)
                continue;
            if (item.getType() == Material.FILLED_MAP) {
                if (!item.hasItemMeta())
                    player.sendMessage("[SignMap] Something went wrong, please contact plugin developer.");
                ItemMeta itemMeta = item.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                if (container.has(plugin.getKeyUUID(), new PersistentUUID())) {
                    failed += item.getAmount();
                    continue;
                }
                container.set(plugin.getKeyUUID(), new PersistentUUID(), uuid);
                container.set(plugin.getKeyName(), PersistentDataType.STRING, authorName);
                // And the text which we'll put in item's lore:
                String loreText = MessageFormat.format(plugin.getLocale().FORMAT_LORE_TEXT, authorName);
                container.set(plugin.getKeyLore(), PersistentDataType.STRING, ChatColor.stripColor(loreText));
                // We also add lore line saying who signed the map:
                List<String> lore = ((lore = itemMeta.getLore()) != null) ? lore : new ArrayList<>();
                lore.add(loreText);
                itemMeta.setLore(lore);
                // Saving changes to item:
                item.setItemMeta(itemMeta);
                succeeded += item.getAmount();
            }
        }

        if (succeeded == 0) {
            player.sendMessage(plugin.getLocale().ERROR_ALL_MAPS_ALREADY_SIGNED);
            return;
        }
        int total = succeeded + failed;
        player.sendMessage(plugin.getLocale().replaceMapsPlaceholder(plugin.getLocale().FORMAT_MAPS_FOUND, total));
        player.sendMessage(plugin.getLocale().replaceMapsPlaceholder(plugin.getLocale().FORMAT_SIGNALL_MAPS_SIGNED, succeeded));
        if (failed != 0)
            player.sendMessage(plugin.getLocale().replaceMapsPlaceholder(plugin.getLocale().FORMAT_SIGNALL_MAPS_ALREADY_SIGNED, failed));
    }

    @CommandAlias("unsignall")
    @Description("Unsigns all accessible maps in player's inventory")
    @CommandPermission("signmap.unsignall")
    public static void onUnsignall(Player player) {
        // Let's look through player's inventory, and if there are no maps, show an error.
        PlayerInventory inv = player.getInventory();
        if (!inv.contains(Material.FILLED_MAP)) {
            player.sendMessage(plugin.getLocale().ERROR_NO_MAPS_IN_INVENTORY);
            return;
        }

        // Otherwise, let's prepare player's uuid to check if they're allowed to /unsign.
        UUID uuid = player.getUniqueId();

        // And iterate over inventory
        int succeeded = 0, not_signed = 0, signed_by_another = 0;
        for (ItemStack item : inv.getContents()) {
            if (item == null)
                continue;
            if (item.getType() == Material.FILLED_MAP) {
                if (!item.hasItemMeta())
                    player.sendMessage("[SignMap] Something went wrong, please contact plugin developer.");
                ItemMeta itemMeta = item.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                if (!container.has(plugin.getKeyUUID(), new PersistentUUID())) {
                    not_signed += item.getAmount();
                    continue;
                }
                UUID authorUUID = container.get(plugin.getKeyUUID(), new PersistentUUID());
                if (!uuid.equals(authorUUID) && !player.hasPermission("signmap.unsign.others")) {
                    signed_by_another += item.getAmount();
                    continue;
                }
                // Removing data from persistent container.
                container.remove(plugin.getKeyUUID());
                if (container.has(plugin.getKeyName(), PersistentDataType.STRING)) {
                    container.remove(plugin.getKeyName());
                }
                // If there's lore text saved in persistent container, save it before removing data.
                List<String> loreToRemove = new ArrayList<>();
                if (container.has(plugin.getKeyLore(), PersistentDataType.STRING)) {
                    loreToRemove.add(ChatColor.stripColor(container.get(plugin.getKeyLore(), PersistentDataType.STRING)));
                    container.remove(plugin.getKeyLore());
                }
                // If item was signed before locale.yml and 'lore-text' key were introduced, also look for an outdated text:
                loreToRemove.add("Signed by ");
                // And, finally, let's remove custom lore we've added in past from the item:
                List<String> lore = itemMeta.getLore();
                if (lore != null) {
                    lore.removeIf(s -> loreToRemove.stream().anyMatch(l -> ChatColor.stripColor(s).startsWith(l)));
                    itemMeta.setLore(lore);
                }
                // Saving changes:
                item.setItemMeta(itemMeta);
                succeeded += item.getAmount();
            }
        }

        if (succeeded == 0 && signed_by_another == 0) {
            player.sendMessage(plugin.getLocale().ERROR_ALL_MAPS_NOT_SIGNED);
            return;
        }
        if (succeeded == 0 && not_signed == 0) {
            player.sendMessage(plugin.getLocale().ERROR_ALL_MAPS_SIGNED_BY_ANOTHER);
            return;
        }
        int total = succeeded + not_signed + signed_by_another;
        player.sendMessage(plugin.getLocale().replaceMapsPlaceholder(plugin.getLocale().FORMAT_MAPS_FOUND, total));
        if (succeeded != 0)
            player.sendMessage(plugin.getLocale().replaceMapsPlaceholder(plugin.getLocale().FORMAT_UNSIGNALL_MAPS_UNSIGNED, succeeded));
        if (not_signed != 0)
            player.sendMessage(plugin.getLocale().replaceMapsPlaceholder(plugin.getLocale().FORMAT_UNSIGNALL_MAPS_NOT_SIGNED, not_signed));
        if (signed_by_another != 0)
            player.sendMessage(plugin.getLocale().replaceMapsPlaceholder(plugin.getLocale().FORMAT_UNSIGNALL_MAPS_SIGNED_BY_ANOTHER, signed_by_another));
    }
}
