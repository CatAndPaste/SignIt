package ru.alskar.signmap;
import co.aikar.commands.PaperCommandManager;
import de.jeff_media.updatechecker.UpdateChecker;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.alskar.signmap.commands.CommandSignMap;
import ru.alskar.signmap.commands.SubcommandSignMap;
import ru.alskar.signmap.config.Config;
import ru.alskar.signmap.config.Locale;
import ru.alskar.signmap.handlers.ConfigManager;
import ru.alskar.signmap.listeners.ListenerMapCloning;
import ru.alskar.signmap.misc.Broadcasts;

public class SignMap extends JavaPlugin {

    @Getter private static SignMap instance;
    @Getter private final NamespacedKey keyUUID = new NamespacedKey(this, "author-uuid");
    @Getter private final NamespacedKey keyName = new NamespacedKey(this, "author-name");
    @Getter private final NamespacedKey keyLore = new NamespacedKey(this, "lore-text");
    @Getter private final ConfigManager configManager = new ConfigManager(this);

    // CHANGE THIS:
    @Getter @Setter private FileConfiguration settings;

    @Getter @Setter private Locale locale;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        configManager.createConfig();
        // Adding Metrics by bStats:
        final int pluginId = 12054;
        Metrics metrics = new Metrics(this, pluginId);
        // Adding update checker by Jeff Media:
        final int spigotResourceID = 99999;
        UpdateChecker.init(this, spigotResourceID)
                .suppressUpToDateMessage(true)
                .checkEveryXHours(24)
                .setNotifyOpsOnJoin(false)
                .checkNow();
        // Registering commands:
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.getCommandReplacements().addReplacement("signmap", "signmap|" +
                this.getConfig().getString("signmap-aliases"));
        commandManager.getCommandReplacements().addReplacement("unsignmap", "unsignmap|" +
                this.getConfig().getString("unsignmap-aliases"));
        commandManager.registerCommand(new CommandSignMap());
        commandManager.registerCommand(new SubcommandSignMap());
        // And listeners:
        getServer().getPluginManager().registerEvents(new ListenerMapCloning(), this);
        // Drawing cute kitten. For fun.
        if (this.getConfig().getBoolean(Config.GREETING_KITTEN)) {
            this.getLogger().info("[SignMap] Hey! What is it?\n" +
                    "\n──────────▄▀▄─────────▄▀▄" +
                    "\n────────▄█░░▀▀▀▀▀░░█▄" +
                    "\n──▄▄──█░░░░░░░░░░░█──▄▄" +
                    "\n█▄▄█─█░░▀░░┬░░▀░░█─█▄▄█");
            this.getLogger().info("[SignMap] Oh, I see. Just two cute kittens looking at each other!");
        }
    }

    public void log(String message) {
        log(Broadcasts.INFO, message);
    }

    public void log(int broadcastType, String message) {
        if (broadcastType == 1) {
            this.getLogger().warning("[SignMap] " + message);
        }
        else if (broadcastType == 2) {
            this.getLogger().severe("[SignMap] " + message);
        }
        else
            this.getLogger().info("[SignMap] " + message);
    }
}