package org.example.h4dro.worthAPI;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private FileConfiguration config;

    public ConfigManager(org.bukkit.configuration.file.FileConfiguration config) {
        this.config = config;
    }

    public void reload(org.bukkit.configuration.file.FileConfiguration config) {
        this.config = config;
    }

    public Double getPrice(String materialName) {
        if (config.contains("items." + materialName)) {
            return config.getDouble("items." + materialName);
        }
        return null;
    }

    public void updatePrice(String materialName, double price) {
        config.set("items." + materialName, price);
    }
}

