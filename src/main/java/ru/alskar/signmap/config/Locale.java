package ru.alskar.signmap.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.alskar.signmap.SignMap;

import java.io.File;
import java.io.IOException;

public class Locale {
    public final String PREFIX;
    public final String ERROR_NO_MAP_IN_HAND_TO_SIGN;
    public final String ERROR_NO_MAP_IN_HAND_TO_UNSIGN;
    public final String ERROR_MAP_ALREADY_SIGNED;
    public final String ERROR_MAP_NOT_SIGNED;
    public final String ERROR_NOT_ALLOWED_TO_UNSIGN;
    public final String ERROR_NOT_ALLOWED_TO_COPY;
    public final String SUCCESSFULLY_SIGNED;
    public final String SUCCESSFULLY_UNSIGNED;
    public final String ERROR_NO_MAPS_IN_INVENTORY;
    public final String ERROR_ALL_MAPS_ALREADY_SIGNED;
    public final String ERROR_ALL_MAPS_NOT_SIGNED;
    public final String ERROR_ALL_MAPS_SIGNED_BY_ANOTHER;
    public final String MAPS_FOUND;
    public final String SIGNALL_MAPS_SIGNED;
    public final String SIGNALL_MAPS_ALREADY_SIGNED;
    public final String UNSIGNALL_MAPS_UNSIGNED;
    public final String UNSIGNALL_MAPS_NOT_SIGNED;
    public final String UNSIGNALL_MAPS_SIGNED_BY_ANOTHER;
    public final String CONFIG_RELOADED;
    public final String LORE_TEXT;
    public final String UNKNOWN_PLAYER;
    public final String _MAP;
    public final String _MAPS;

    private final YamlConfiguration messages;
    private final boolean showPrefix;

    public Locale(SignMap plugin, String language) {
        File messagesFile = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator + language + ".yml");

        messages = new YamlConfiguration();
        try {
            messages.load(messagesFile);
        } catch (IOException e){
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        // Add prefix to messages if it's not empty in language file and enabled in config:
        showPrefix = plugin.getConfig().getBoolean(Config.SHOW_PREFIX) &&
                !messages.getString("message-prefix", "").isEmpty();

        this.PREFIX = ChatColor.translateAlternateColorCodes('&',
                messages.getString("message-prefix", "&7[&fSign&cMap&7]") + " ");

        this.ERROR_NO_MAP_IN_HAND_TO_SIGN = buildPrefixedMessage("message-error-no-map-to-sign",
                        "&cYou need to have the map you want to sign in your main hand!");
        this.ERROR_NO_MAP_IN_HAND_TO_UNSIGN = buildPrefixedMessage("message-error-no-map-to-unsign",
                        "&cYou need to have the map you want to unsign in your main hand!");
        this.ERROR_MAP_ALREADY_SIGNED = buildPrefixedMessage("message-error-map-already-signed",
                        "&cMap is already signed by %s.");
        this.ERROR_MAP_NOT_SIGNED = buildPrefixedMessage("message-error-map-not-signed",
                        "&cThis map is not signed!");
        this.ERROR_NOT_ALLOWED_TO_UNSIGN = buildPrefixedMessage("message-error-not-allowed-to-unsign",
                        "&cHey, only player that signed this map or server operators can unsign it!");
        this.ERROR_NOT_ALLOWED_TO_COPY = buildPrefixedMessage("message-error-not-allowed-to-copy",
                        "&cHey, you cannot copy this map! It was signed by %s.");
        this.SUCCESSFULLY_UNSIGNED = buildPrefixedMessage("message-unsigned-successfully",
                        "&aMap unsigned successfully! Anyone can copy it now!");
        this.SUCCESSFULLY_SIGNED = buildPrefixedMessage("message-signed-successfully",
                        "&aMap signed successfully! Author is now set to &e%s&a.");

        this.ERROR_NO_MAPS_IN_INVENTORY = buildPrefixedMessage("message-error-no-maps-in-inventory",
                "&cThere are no maps found in your inventory!");
        this.ERROR_ALL_MAPS_ALREADY_SIGNED = buildPrefixedMessage("message-error-all-maps-signed",
                "&cAll maps in your inventory already signed!");
        this.ERROR_ALL_MAPS_NOT_SIGNED = buildPrefixedMessage("message-error-all-maps-not-signed",
                "&cMaps in your inventory are not signed!");
        this.ERROR_ALL_MAPS_SIGNED_BY_ANOTHER = buildPrefixedMessage("message-error-all-maps-signed-by-someone-else",
                "&cAll maps in your inventory already signed by another players!");

        this.MAPS_FOUND = buildPrefixedMessage("maps-found-in-inventory",
                "&aI looked through your inventory and found %d maps:");
        this.SIGNALL_MAPS_SIGNED = buildMessage("message-maps-signed-successfully",
                "&aSuccessfully signed %d maps.");
        this.SIGNALL_MAPS_ALREADY_SIGNED = buildMessage("message-maps-already-signed",
                "&e%d maps were already signed, skipped them.");
        this.UNSIGNALL_MAPS_UNSIGNED = buildMessage("message-maps-unisigned-successfully",
                "&eSuccessfully unsigned %d maps.");
        this.UNSIGNALL_MAPS_NOT_SIGNED = buildMessage("message-maps-not-signed",
                "&e%d maps were not signed, skipped them.");
        this.UNSIGNALL_MAPS_SIGNED_BY_ANOTHER = buildMessage("message-maps-signed-by-someone-else",
                "&e%d maps were signed by someone else, skipped them.");

        this._MAP = buildMessage("message-placeholder-map", "map");
        this._MAPS = buildMessage("message-placeholder-maps", "maps");

        this.CONFIG_RELOADED = buildPrefixedMessage("message-reloaded",
                "&aConfiguration and {language}.yml files reloaded!");

        this.LORE_TEXT = buildMessage("lore-text", "&7Signed by &6%s");
        this.UNKNOWN_PLAYER = buildMessage("unknown-player", "unknown player");
    }

    private String buildMessage(String path, String def) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(path, def));
    }

    private String buildPrefixedMessage(String path, String def) {
        return ((showPrefix) ? this.PREFIX : "") +
                ChatColor.translateAlternateColorCodes('&', messages.getString(path, def));
    }
}
