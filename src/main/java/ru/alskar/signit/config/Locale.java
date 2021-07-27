package ru.alskar.signit.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.alskar.signit.SignIt;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class Locale {
    public final String PREFIX;
    public final String ERROR_NO_MAP_IN_HAND_TO_SIGN;
    public final String ERROR_NO_MAP_IN_HAND_TO_UNSIGN;
    public final String ERROR_MAP_NOT_SIGNED;
    public final String ERROR_NOT_ALLOWED_TO_UNSIGN;
    public final String ERROR_NO_MAPS_IN_INVENTORY;
    public final String ERROR_ALL_MAPS_ALREADY_SIGNED;
    public final String ERROR_ALL_MAPS_NOT_SIGNED;
    public final String ERROR_ALL_MAPS_SIGNED_BY_ANOTHER;
    public final String SUCCESSFULLY_UNSIGNED;
    public final String CONFIG_RELOADING;

    public final String FORMAT_ERROR_MAP_ALREADY_SIGNED;
    public final String FORMAT_ERROR_NOT_ALLOWED_TO_COPY;
    public final String FORMAT_SUCCESSFULLY_SIGNED;
    public final String FORMAT_CONFIG_RELOADED;
    public final String FORMAT_MAPS_FOUND;
    public final String FORMAT_SIGNALL_MAPS_SIGNED;
    public final String FORMAT_SIGNALL_MAPS_ALREADY_SIGNED;
    public final String FORMAT_UNSIGNALL_MAPS_UNSIGNED;
    public final String FORMAT_UNSIGNALL_MAPS_NOT_SIGNED;
    public final String FORMAT_UNSIGNALL_MAPS_SIGNED_BY_ANOTHER;
    public final String FORMAT_LORE_TEXT;

    public final String PH_UNKNOWN_PLAYER;
    public final String PH_MAP;
    public final String PH_MAPS;

    private final YamlConfiguration messages;
    private final boolean showPrefix;

    public Locale(SignIt plugin, String language) {
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
                messages.getString("message-prefix", "&7[&fSign&cIt&7]") + " ");

        this.ERROR_NO_MAP_IN_HAND_TO_SIGN = buildPrefixedMessage("message-error-no-map-to-sign",
                        "&cYou need to have the map you want to sign in your main hand!");
        this.ERROR_NO_MAP_IN_HAND_TO_UNSIGN = buildPrefixedMessage("message-error-no-map-to-unsign",
                        "&cYou need to have the map you want to unsign in your main hand!");
        this.ERROR_MAP_NOT_SIGNED = buildPrefixedMessage("message-error-map-not-signed",
                        "&cThis map is not signed!");
        this.ERROR_NOT_ALLOWED_TO_UNSIGN = buildPrefixedMessage("message-error-not-allowed-to-unsign",
                        "&cHey, only player that signed this map or server operators can unsign it!");
        this.ERROR_NO_MAPS_IN_INVENTORY = buildPrefixedMessage("message-error-no-maps-in-inventory",
                "&cThere are no maps found in your inventory!");
        this.ERROR_ALL_MAPS_ALREADY_SIGNED = buildPrefixedMessage("message-error-all-maps-signed",
                "&cAll maps in your inventory already signed!");
        this.ERROR_ALL_MAPS_NOT_SIGNED = buildPrefixedMessage("message-error-all-maps-not-signed",
                "&cMaps in your inventory are not signed!");
        this.ERROR_ALL_MAPS_SIGNED_BY_ANOTHER = buildPrefixedMessage("message-error-all-maps-signed-by-someone-else",
                "&cAll maps in your inventory already signed by another players!");
        this.SUCCESSFULLY_UNSIGNED = buildPrefixedMessage("message-unsigned-successfully",
                "&aMap unsigned successfully! Anyone can copy it now!");
        this.CONFIG_RELOADING = buildPrefixedMessage("message-reloading",
                "&7Configuration and language files are reloading...");

        this.PH_UNKNOWN_PLAYER = buildMessage("unknown-player", "unknown player");
        this.PH_MAP = buildMessage("message-placeholder-map", "map");
        this.PH_MAPS = buildMessage("message-placeholder-maps", "maps");

        this.FORMAT_ERROR_MAP_ALREADY_SIGNED = buildPrefixedMessage("message-error-map-already-signed",
                "&cMap is already signed by {author}.")
                .replace("{author}", "{0}");
        this.FORMAT_ERROR_NOT_ALLOWED_TO_COPY = buildPrefixedMessage("message-error-not-allowed-to-copy",
                "&cHey, you cannot copy this map! It was signed by {author}.")
                .replace("{author}", "{0}");
        this.FORMAT_SUCCESSFULLY_SIGNED = buildPrefixedMessage("message-signed-successfully",
                "&aMap signed successfully! Author is now set to &e{author}&a.")
                .replace("{author}", "{0}");
        this.FORMAT_MAPS_FOUND = buildPrefixedMessage("message-maps-found-in-inventory",
                "&aI looked through your inventory and found {amount} {map(s)}:")
                .replace("{amount}", "{0}").replace("{map(s)}", "{1}");
        this.FORMAT_SIGNALL_MAPS_SIGNED = buildMessage("message-maps-signed-successfully",
                "&aSuccessfully signed {amount} {map(s)}.")
                .replace("{amount}", "{0}").replace("{map(s)}", "{1}");
        this.FORMAT_SIGNALL_MAPS_ALREADY_SIGNED = buildMessage("message-maps-already-signed",
                "&e{amount} {map(s)} were already signed, skipped them.")
                .replace("{amount}", "{0}").replace("{map(s)}", "{1}");
        this.FORMAT_UNSIGNALL_MAPS_UNSIGNED = buildMessage("message-maps-unisigned-successfully",
                "&eSuccessfully unsigned {amount} {map(s)}.")
                .replace("{amount}", "{0}").replace("{map(s)}", "{1}");
        this.FORMAT_UNSIGNALL_MAPS_NOT_SIGNED = buildMessage("message-maps-not-signed",
                "&e{amount} {map(s)} were not signed, skipped them.")
                .replace("{amount}", "{0}").replace("{map(s)}", "{1}");
        this.FORMAT_UNSIGNALL_MAPS_SIGNED_BY_ANOTHER = buildMessage("message-maps-signed-by-someone-else",
                "&e{amount} {map(s)} were signed by someone else, skipped them.")
                .replace("{amount}", "{0}").replace("{map(s)}", "{1}");
        this.FORMAT_CONFIG_RELOADED = buildPrefixedMessage("message-reloaded",
                "&aConfiguration and {language}.yml files reloaded!")
                .replace("{language}", "{0}");
        this.FORMAT_LORE_TEXT = buildMessage("lore-text", "&7Signed by &6{author}")
                .replace("{author}", "{0}");
    }

    public String replaceMapsPlaceholder(String message, int amount) {
        return MessageFormat.format(message, amount, (amount == 1) ? this.PH_MAP : this.PH_MAPS);
    }

    private String buildMessage(String path, String def) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(path, def));
    }

    private String buildPrefixedMessage(String path, String def) {
        return ((showPrefix) ? this.PREFIX : "") +
                ChatColor.translateAlternateColorCodes('&', messages.getString(path, def));
    }
}
