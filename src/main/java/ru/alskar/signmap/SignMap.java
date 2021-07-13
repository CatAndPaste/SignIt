package ru.alskar.signmap;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class SignMap extends JavaPlugin {

    @Getter private final NamespacedKey keyUUID = new NamespacedKey(this, "author-uuid");
    @Getter private final NamespacedKey keyName = new NamespacedKey(this, "author-name");
    @Getter private static SignMap instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new MainCommand());
        // And Listeners:
        getServer().getPluginManager().registerEvents(new ListenerMapCopying(), this);
        // Drawing cute kitten. For fun.
        this.getLogger().info("[SignMap] Hey! What is it?\n" +
                "\n──────────▄▀▄─────────▄▀▄" +
                "\n────────▄█░░▀▀▀▀▀░░█▄" +
                "\n──▄▄──█░░░░░░░░░░░█──▄▄" +
                "\n█▄▄█─█░░▀░░┬░░▀░░█─█▄▄█");
        this.getLogger().info("[SignMap] Oh, I see. Just two cute kittens looking at each other!");
    }
}