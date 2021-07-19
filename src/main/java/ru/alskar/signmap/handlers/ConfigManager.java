package ru.alskar.signmap.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.alskar.signmap.SignMap;
import ru.alskar.signmap.config.Config;
import ru.alskar.signmap.config.Locale;

import java.io.*;
import java.util.*;

public class ConfigManager {

    final SignMap plugin;

    private int previousConfigVersion = 0, currentConfigVersion = 0;
    private int previousLocaleVersion = 0, currentLocaleVersion = 0;

    final private List<String> ignoreList;

    public ConfigManager(SignMap plugin) {
        this.plugin = plugin;
        ignoreList = new ArrayList<>(Arrays.asList("config-version", "locale-version"));
    }

    private String getFilePath(String fileName) {
        return plugin.getDataFolder() + File.separator + fileName;
    }

    // Checks versions of config file and active localisation:
    private void checkVersions() {
        FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(new File(getFilePath("config.yml")));
        FileConfiguration newConfig =
                YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));

        previousConfigVersion = oldConfig.getInt("config-version");
        currentConfigVersion = newConfig.getInt("config-version");

        String lang = oldConfig.getString(Config.LOCALE);

        FileConfiguration oldLocale = YamlConfiguration.loadConfiguration(new File(getFilePath("languages"
                + File.separator + lang + ".yml")));
        InputStream localeResource = (localeResource = plugin.getResource("languages/" + lang +".yml")) == null ?
                plugin.getResource("languages/english.yml") : localeResource;
        FileConfiguration newLocale =
                YamlConfiguration.loadConfiguration(new InputStreamReader(localeResource));

        previousLocaleVersion = oldLocale.getInt("locale-version");
        currentLocaleVersion = newLocale.getInt("locale-version");
    }

    // Replaces value in freshly made config file with one from the old configuration:
    private String makeReplacement(String str, Map<String, Object> replacementMap, boolean stringQuotes) {
        String[] split = str.split(":");
        if (ignoreList.contains(split[0]))
            return str;
        Object replacement = replacementMap.get(split[0]);
        if (replacement == null)
            return str;
        String newStr;
        if (replacement instanceof String)
            newStr = str.replace(split[1], " \"" + replacement + "\"");
        else
            newStr = str.replace(split[1], " " + replacement.toString());
        plugin.log("Compiling: " + newStr);
        return newStr;
    }

    private void updateYAML(String path, boolean stringQuotes) throws IOException, NullPointerException {
        updateYAML(path, path, stringQuotes);
    }

    // Shallow search YAML file updater:
    // TODO: rewrite code, so 'deep' keys and YAML arrays are supported
    private void updateYAML(String path, String resourcePath, boolean stringQuotes) throws IOException, NullPointerException {
        FileConfiguration oldYAML = YamlConfiguration.loadConfiguration(new File(getFilePath(path)));
        Map<String, Object> oldValues = oldYAML.getValues(false);

        InputStreamReader inputStreamReader = new InputStreamReader(plugin.getResource(resourcePath));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(getFilePath(path).replace("/", File.separator))));
        List<String> lines = new LinkedList<>();
        String line;
        while((line = reader.readLine()) != null)
            lines.add(line);
        reader.close();
        for (int i=0; i < lines.size(); i++) {
            line = lines.get(i);
            if (line.contains(":"))
                lines.set(i, makeReplacement(line, oldValues, stringQuotes));
        }
        for (String out:lines) {
            writer.write(out);
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    public void createConfig() {
        plugin.saveDefaultConfig();
        saveLanguageFiles();

        checkVersions();

        if (previousConfigVersion < currentConfigVersion) {
            try {
                plugin.getConfigManager().updateYAML("config.yml", false);
                plugin.reloadConfig();
                plugin.log(String.format("Configuration file updated successfully from version %d to %d!",
                        previousConfigVersion,
                        currentConfigVersion));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String lang = plugin.getConfig().getString("language");

        if (previousLocaleVersion < currentLocaleVersion) {
            try {
                if (plugin.getResource("languages/" + lang + ".yml") == null)
                    plugin.getConfigManager().updateYAML("languages" + "/" + lang + ".yml", "languages/english.yml", true);
                else
                    plugin.getConfigManager().updateYAML("languages" + "/" + lang + ".yml", true);
                plugin.log(String.format("Language file %s.yml updated successfully from version %d to %d!",
                        lang,
                        previousConfigVersion,
                        currentConfigVersion));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        plugin.setLocale(new Locale(plugin, lang));
    }

    public void reloadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        saveLanguageFiles();
        String lang = plugin.getConfig().getString(Config.LOCALE);
        plugin.setLocale(new Locale(plugin, lang));
    }

    public void saveLanguageFiles() {
        File dir = new File(plugin.getDataFolder() + File.separator + "languages");
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();
        List<String> locales = new ArrayList<>();
        locales.add("english");
        locales.add("russian");
        locales.add("chinese");
        for (String lang : locales)
            saveLanguageFile(lang);
        String activeLocale = plugin.getConfig().getString(Config.LOCALE);
        if (!locales.contains(activeLocale))
            saveLanguageFile(activeLocale);
    }

    private void saveLanguageFile(String fileName) {
        File f = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator + fileName + ".yml");
        if(!f.exists() || f.isDirectory())
            if (plugin.getResource("languages/" + fileName + ".yml") != null)
                plugin.saveResource("languages/" + fileName + ".yml", false);
            else {
                InputStreamReader inputStreamReader = new InputStreamReader(plugin.getResource("languages/english.yml"));
                try {
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                    List<String> lines = new LinkedList<>();
                    String line;
                    while((line = reader.readLine()) != null)
                        lines.add(line);
                    reader.close();
                    for (String out:lines) {
                        writer.write(out);
                        writer.newLine();
                    }
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
