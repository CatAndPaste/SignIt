package ru.alskar.signmap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandSignMap implements CommandExecutor {

    private SignMap signMap;

    public CommandSignMap(SignMap signMap) {
        this.signMap = signMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getInventory().getItemInMainHand().getType() == Material.FILLED_MAP) {
                // Let's now look closer at the map in player's hand!
                ItemStack map = player.getInventory().getItemInMainHand();
                ItemMeta mapMeta = map.getItemMeta();
                PersistentDataContainer container = mapMeta.getPersistentDataContainer();
                // Has it already been signed by anyone?
                if (container.has(signMap.keyUUID, new PersistentUUID())) {
                    if (container.has(signMap.keyName, PersistentDataType.STRING)) {
                        String authorName = container.get(signMap.keyName, PersistentDataType.STRING);
                        player.sendMessage(String.format("§cMap is already signed by %s.", authorName));
                    } else {
                        player.sendMessage("§cMap is already signed by unknown player.");
                    }
                }
                // No? Let's sign it then!
                else {
                    // Writing player's UUID into persistent container of an item:
                    UUID uuid = player.getUniqueId();
                    container.set(signMap.keyUUID, new PersistentUUID(), uuid);
                    // Also, we write player's name,
                    // because I'm a lazy ass to read about how to get the name off UUID, sorry :/
                    String authorName = player.getDisplayName();
                    container.set(signMap.keyName, PersistentDataType.STRING, authorName);
                    // We also add lore line saying who signed the map:
                    List<String> lore = ((lore = mapMeta.getLore()) != null) ? lore : new ArrayList();
                    lore.add("§7Signed by §6" + authorName);
                    mapMeta.setLore(lore);
                    map.setItemMeta(mapMeta);
                    player.sendMessage(String.format("§aMap signed successfully! Author is now set to §e%s§a.",
                            authorName));
                }
            } else {
                player.sendMessage("§cYou need to have the map you want to sign in your main hand!");
            }
        } else {
            signMap.getLogger().warning("[SignMap] This command is for players only!");
        }

        return true;
    }
}

