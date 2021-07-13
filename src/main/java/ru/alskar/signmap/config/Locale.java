package ru.alskar.signmap.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.alskar.signmap.SignMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    public final String LORE_TEXT;
    public final String UNKNOWN_PLAYER;

    public Locale(SignMap plugin) {
        plugin.saveResource("locale.yml", false);
        File messagesFile = new File(plugin.getDataFolder(), "locale.yml");

        YamlConfiguration messages = new YamlConfiguration();
        try{
            messages.load(messagesFile);
        } catch (IOException e){
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        boolean showPrefix = false;
        if (plugin.getConfig().getBoolean(Config.SHOW_PREFIX)) {
            // Just in case if someone decided to disable prefix this unusual way, let's just disable it.
            if (!messages.getString("message-prefix", "").isEmpty())
                showPrefix = true;
        }

        this.PREFIX = ChatColor.translateAlternateColorCodes('&',
                messages.getString("message-prefix", "&7[&fSign&cMap&7]") + " ");

        this.ERROR_NO_MAP_IN_HAND_TO_SIGN = MessageBuilder(messages,"message-error-no-map-to-sign",
                        "&cYou need to have the map you want to sign in your main hand!", showPrefix);
        this.ERROR_NO_MAP_IN_HAND_TO_UNSIGN = MessageBuilder(messages,"message-error-no-map-to-unsign",
                        "§cYou need to have the map you want to unsign in your main hand!", showPrefix);
        this.ERROR_MAP_ALREADY_SIGNED = MessageBuilder(messages,"message-error-map-already-signed",
                        "&cMap is already signed by %s.", showPrefix);
        this.ERROR_MAP_NOT_SIGNED = MessageBuilder(messages,"message-error-map-not-signed",
                        "&cThis map is not signed!", showPrefix);
        this.ERROR_NOT_ALLOWED_TO_UNSIGN = MessageBuilder(messages,"message-error-not-allowed-to-unsign",
                        "&cHey, only player that signed this map or server Operator can unsign it!", showPrefix);
        this.ERROR_NOT_ALLOWED_TO_COPY = MessageBuilder(messages,"message-error-not-allowed-to-copy",
                        "&cHey, you cannot copy this map! It was signed by %s.", showPrefix);
        this.SUCCESSFULLY_UNSIGNED = MessageBuilder(messages,"message-unsigned-successfully",
                        "&aMap unsigned successfully, anyone can copy it now!", showPrefix);
        this.SUCCESSFULLY_SIGNED = MessageBuilder(messages,"message-signed-successfully",
                        "&aMap signed successfully! Author is now set to &e%s&a.", showPrefix);

        this.LORE_TEXT = MessageBuilder(messages,"lore-text", "&7Signed by &6%s");
        this.UNKNOWN_PLAYER = MessageBuilder(messages,"unknown-player", "unknown player");
    }

    private String MessageBuilder(YamlConfiguration messages, String path, String def) {
        return MessageBuilder(messages, path, def, false);
    }

    private String MessageBuilder(YamlConfiguration messages, String path, String def, boolean prefix) {
        return ((prefix) ? this.PREFIX : "") +
                ChatColor.translateAlternateColorCodes('&', messages.getString(path, def));
    }
}
