package ru.alskar.signmap.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alskar.signmap.SignMap;
import ru.alskar.signmap.config.Config;

import java.text.MessageFormat;

@CommandAlias("signmap")
public class SubcommandSignMap extends BaseCommand {

    private static final SignMap plugin = SignMap.getInstance();

    @Subcommand("reload")
    @Description("Reloads plugin configuration and language files")
    @CommandPermission("signmap.reload")
    public static void onReload(CommandSender sender) {
        sender.sendMessage((sender instanceof Player)
                ? plugin.getLocale().CONFIG_RELOADING
                : ChatColor.stripColor(plugin.getLocale().CONFIG_RELOADING));
        plugin.getConfigManager().reloadConfig();
        sender.sendMessage((sender instanceof Player)
                ? MessageFormat.format(plugin.getLocale().FORMAT_CONFIG_RELOADED, plugin.getConfig().getString(Config.LOCALE))
                : ChatColor.stripColor(MessageFormat.format(plugin.getLocale().FORMAT_CONFIG_RELOADED, plugin.getConfig().getString(Config.LOCALE)))); // CHANGE TO CONFIG_RELOADED_CONSOLE
    }
}
