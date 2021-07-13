package ru.alskar.signmap;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class CommandUnsignMap implements CommandExecutor {

    private final SignMap signMap;

    public CommandUnsignMap(SignMap signMap) {
        this.signMap = signMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getInventory().getItemInMainHand().getType() == Material.FILLED_MAP) {
                // Let's now look closer at the map!
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemMeta itemMeta = item.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                // Has it already been signed by anyone?
                if (container.has(signMap.keyUUID, new PersistentUUID())) {
                    UUID uuid = player.getUniqueId();
                    UUID authorUUID = container.get(signMap.keyUUID, new PersistentUUID());
                    if (uuid.equals(authorUUID) || player.isOp() || player.hasPermission("signmap.unsign")) {
                        // Removing data from persistent container
                        container.remove(signMap.keyUUID);
                        if (container.has(signMap.keyName, PersistentDataType.STRING)) {
                            container.remove(signMap.keyName);
                        }
                        // And removing "Signed by" line in lore
                        List<String> lore = itemMeta.getLore();
                        if (lore != null) {
                            lore.removeIf(s -> s.contains("§7Signed by §6"));
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                        }
                        player.sendMessage("§aMap unsigned successfully, anyone can copy it now!");
                    } else {
                        player.sendMessage("§cHey, only player that signed this map or server Operator can unsign it!");
                    }
                } else {
                    player.sendMessage("§cThis map is not signed!");
                }
            } else {
                player.sendMessage("§cYou need to have the map you want to unsign in your main hand!");
            }
        } else {
            signMap.getLogger().warning("[SignMap] This command is for players only!");
        }
        return true;
    }
}
