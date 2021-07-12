package ru.alskar.signmap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.CartographyInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ListenerMapCopying implements Listener {

    private SignMap signMap;

    public ListenerMapCopying(SignMap signMap) {
        this.signMap = signMap;
    }

    @EventHandler
    public void onNormalCraft(PrepareItemCraftEvent e) {
        if (e.getInventory() == null)
            return;
        if (e.getInventory().getResult() == null)
            return;
        Material itemType = e.getInventory().getResult().getType();
        if (itemType == Material.FILLED_MAP) {
            ItemStack resultedItem = e.getInventory().getResult();
            ItemMeta itemMeta = resultedItem.getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(signMap.keyUUID, new PersistentUUID())) {
                HumanEntity viewer = e.getView().getPlayer();
                if (!(viewer instanceof Player)) {
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                    signMap.getLogger().warning("[SignMap] Seems like non-player entity tried to copy map, " +
                            "operation denied.");
                    return;
                }
                UUID uuid = viewer.getUniqueId();
                UUID authorUUID = container.get(signMap.keyUUID, new PersistentUUID());
                if (!uuid.equals(authorUUID)) {
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                    Player player = (Player) e.getView().getPlayer();
                    String author = container.has(signMap.keyName, PersistentDataType.STRING) ?
                                    container.get(signMap.keyName, PersistentDataType.STRING) : "unknown player";
                    player.sendMessage(String.format("§cHey, you cannot copy this map! It was signed by %s.", author));
                }
            }
        }
    }

    @EventHandler
    public void onCartographerTable(InventoryClickEvent e) {
        if (e.getInventory() instanceof CartographyInventory) {
            ItemStack item = e.getCurrentItem();
            if (item == null)
                return;
            if (item.getType() == Material.FILLED_MAP) {
                ItemMeta itemMeta = item.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                if (container.has(signMap.keyUUID, new PersistentUUID())) {
                    HumanEntity viewer = e.getView().getPlayer();
                    if (!(viewer instanceof Player)) {
                        e.setCancelled(true);
                        signMap.getLogger().warning("[SignMap] Seems like non-player entity tried to copy a map " +
                                "using Cartography Table, operation denied.");
                        return;
                    }
                    UUID uuid = viewer.getUniqueId();
                    UUID authorUUID = container.get(signMap.keyUUID, new PersistentUUID());
                    if (!uuid.equals(authorUUID)) {
                        e.setCancelled(true);
                        Player player = (Player) e.getView().getPlayer();
                        String author = container.has(signMap.keyName, PersistentDataType.STRING) ?
                                container.get(signMap.keyName, PersistentDataType.STRING) : "unknown player";
                        player.sendMessage(String.format("§cHey, you cannot copy this map! It was signed by %s.",
                                author));
                    }
                }
            }
        }
    }
}
