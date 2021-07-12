package ru.alskar.signmap;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class SignMap extends JavaPlugin {
    NamespacedKey keyUUID;
    NamespacedKey keyName;

    @Override
    public void onEnable() {
        // Creating 2 keys for persistent container:
        keyUUID = new NamespacedKey(this, "author-uuid");
        keyName = new NamespacedKey(this, "author-name");
        // Registering commands:
        this.getCommand("signmap").setExecutor(new CommandSignMap(this));
        this.getCommand("unsignmap").setExecutor(new CommandUnsignMap(this));
        // And Listeners:
        getServer().getPluginManager().registerEvents(new ListenerMapCopying(this), this);
        // Drawing cute kitten. For fun.
        this.getLogger().info("[SignMap] Hey! What is it?\n" +
                "\n──────────▄▀▄─────────▄▀▄" +
                "\n────────▄█░░▀▀▀▀▀░░█▄" +
                "\n──▄▄──█░░░░░░░░░░░█──▄▄" +
                "\n█▄▄█─█░░▀░░┬░░▀░░█─█▄▄█");
        this.getLogger().info("[SignMap] Oh, I see. Just two cute kittens looking at each other!");
    }

    @Override
    public void onDisable() {

    }
}