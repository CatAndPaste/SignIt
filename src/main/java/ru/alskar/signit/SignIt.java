package ru.alskar.signit;
import co.aikar.commands.PaperCommandManager;
import de.jeff_media.updatechecker.UpdateChecker;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import ru.alskar.signit.commands.CommandSignMap;
import ru.alskar.signit.commands.SubcommandSignMap;
import ru.alskar.signit.config.Config;
import ru.alskar.signit.config.Locale;
import ru.alskar.signit.handlers.ConfigManager;
import ru.alskar.signit.listeners.ListenerMapCloning;
import ru.alskar.signit.misc.Logs;

public class SignIt extends JavaPlugin {

    @Getter private static SignIt instance;
    @Getter private final NamespacedKey keyUUID = new NamespacedKey(this, "author-uuid");
    @Getter private final NamespacedKey keyName = new NamespacedKey(this, "author-name");
    @Getter private final NamespacedKey keyLore = new NamespacedKey(this, "lore-text");
    @Getter private final ConfigManager configManager = new ConfigManager(this);

    @Getter @Setter private Locale locale;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        configManager.createConfig();
        // Adding Metrics by bStats:
        final int pluginId = 12239;
        Metrics metrics = new Metrics(this, pluginId);
        // Adding update checker by Jeff Media:
        final int spigotResourceID = 94720;
        UpdateChecker.init(this, spigotResourceID)
                .suppressUpToDateMessage(true)
                .checkEveryXHours(24)
                .setNotifyOpsOnJoin(false)
                .checkNow();

        // Registering commands:
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.getCommandReplacements().addReplacement("signit", "signit|" +
                this.getConfig().getString("signit-aliases"));
        commandManager.getCommandReplacements().addReplacement("unsignit", "unsignit|" +
                this.getConfig().getString("unsignit-aliases"));
        commandManager.registerCommand(new CommandSignMap());
        commandManager.registerCommand(new SubcommandSignMap());
        // And listeners:
        getServer().getPluginManager().registerEvents(new ListenerMapCloning(), this);
        // Drawing cute kitten. For fun.
        if (this.getConfig().getBoolean(Config.GREETING_KITTEN)) {
            log("Hey! What is it?\n" +
                    "\n──────────▄▀▄─────────▄▀▄" +
                    "\n────────▄█░░▀▀▀▀▀░░█▄" +
                    "\n──▄▄──█░░░░░░░░░░░█──▄▄" +
                    "\n█▄▄█─█░░▀░░┬░░▀░░█─█▄▄█");
            log("Oh, I see. Just two cute kittens looking at each other!");
        }
    }

    public void log(String message) {
        log(Logs.INFO, message);
    }

    public void log(int broadcastType, String message) {
        if (broadcastType == 1) {
            this.getLogger().warning("[SignIt] " + message);
        }
        else if (broadcastType == 2) {
            this.getLogger().severe("[SignIt] " + message);
        }
        else
            this.getLogger().info("[SignIt] " + message);
    }
}