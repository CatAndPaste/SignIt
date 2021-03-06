package ru.alskar.signit.listeners;

import org.bukkit.Material;
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
import ru.alskar.signit.misc.Logs;
import ru.alskar.signit.types.PersistentUUID;
import ru.alskar.signit.SignIt;

import java.text.MessageFormat;
import java.util.UUID;

public class ListenerMapCloning implements Listener {

    private static final SignIt plugin = SignIt.getInstance();

    @EventHandler
    public void onNormalCraft(PrepareItemCraftEvent e) {
        if (e.getInventory().getResult() == null)
            return;
        Material itemType = e.getInventory().getResult().getType();
        // If result of craft is Filled Map, let's check if it's signed.
        if (itemType == Material.FILLED_MAP) {
            ItemStack resultedItem = e.getInventory().getResult();
            if (!resultedItem.hasItemMeta())
                return;
            ItemMeta itemMeta = resultedItem.getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(plugin.getKeyUUID(), new PersistentUUID())) {
                HumanEntity viewer = e.getView().getPlayer();
                if (!(viewer instanceof Player)) {
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                    plugin.log(Logs.WARN, "[SignIt] Seems like non-player entity tried to copy map, " +
                            "operation denied.");
                    return;
                }
                UUID uuid = viewer.getUniqueId();
                UUID authorUUID = container.get(plugin.getKeyUUID(), new PersistentUUID());
                if (!uuid.equals(authorUUID)) {
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                    Player player = (Player) e.getView().getPlayer();
                    String author = container.has(plugin.getKeyName(), PersistentDataType.STRING) ?
                                    container.get(plugin.getKeyName(), PersistentDataType.STRING) :
                            plugin.getLocale().PH_UNKNOWN_PLAYER;
                    player.sendMessage(MessageFormat.format(plugin.getLocale().FORMAT_ERROR_NOT_ALLOWED_TO_COPY, author));
                }
            }
        }
    }

    @EventHandler
    public void onCartographerTable(InventoryClickEvent e) {
        // Due to API limitations, InventoryClickEvent should be used.
        // If the active inventory is of Cartography Table, let's check if player clicks on a Filled Map.
        if (e.getInventory() instanceof CartographyInventory) {
            ItemStack item = e.getCurrentItem();
            if (item == null)
                return;
            if (item.getType() == Material.FILLED_MAP) {
                if (!item.hasItemMeta())
                    return;
                ItemMeta itemMeta = item.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                // If the map is signed, we check if the player is allowed to copy it.
                if (container.has(plugin.getKeyUUID(), new PersistentUUID())) {
                    HumanEntity viewer = e.getView().getPlayer();
                    if (!(viewer instanceof Player)) {
                        e.setCancelled(true);
                        plugin.log(Logs.WARN, "[SignIt] Seems like non-player entity tried to copy a map " +
                                "using Cartography Table, operation denied.");
                        return;
                    }
                    UUID uuid = viewer.getUniqueId();
                    UUID authorUUID = container.get(plugin.getKeyUUID(), new PersistentUUID());
                    // Otherwise, the map stays in its slot. Might be not the most elegant solution.
                    if (!uuid.equals(authorUUID)) {
                        e.setCancelled(true);
                        Player player = (Player) e.getView().getPlayer();
                        String author = container.has(plugin.getKeyName(), PersistentDataType.STRING) ?
                                container.get(plugin.getKeyName(), PersistentDataType.STRING) :
                                plugin.getLocale().PH_UNKNOWN_PLAYER;
                        player.sendMessage(MessageFormat.format(plugin.getLocale().FORMAT_ERROR_NOT_ALLOWED_TO_COPY, author));
                    }
                }
            }
        }
    }
}
