package ru.alskar.signmap;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import ru.alskar.signmap.commands.CommandSignMap;
import ru.alskar.signmap.config.Config;
import ru.alskar.signmap.config.Locale;
import ru.alskar.signmap.listeners.ListenerMapCloning;

public class SignMap extends JavaPlugin {

    @Getter private final NamespacedKey keyUUID = new NamespacedKey(this, "author-uuid");
    @Getter private final NamespacedKey keyName = new NamespacedKey(this, "author-name");
    @Getter private final NamespacedKey keyLore = new NamespacedKey(this, "lore-text");
    @Getter private final Locale locale = new Locale(this);
    @Getter private static SignMap instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Creating config file:
        this.saveDefaultConfig();
        // Adding Metrics by bStats:
        final int pluginId = 12054;
        Metrics metrics = new Metrics(this, pluginId);
        // Registering commands:
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new CommandSignMap());
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
}