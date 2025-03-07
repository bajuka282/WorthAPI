package org.example.h4dro.worthAPI;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WorthAPI extends JavaPlugin {

    private static WorthAPI instance;
    private Economy econ;
    private ConfigManager configManager;

    public static WorthAPI getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            generateNewConfig();
            getLogger().info("config.yml did not exist, a new one has been created with all Materials.");
        }
        reloadConfig();
        configManager = new ConfigManager(getConfig());

        if (!setupVault()) {
            getLogger().severe("Vault not found! Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("WorthAPI has been enabled!");

        processConfigMaterials();

        updateAllPlayerItems();
        Bukkit.getPluginManager().registerEvents(new ItemLoreListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryUpdateListener(), this);
    }

    private void generateNewConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        TreeMap<String, Double> sortedMaterials = new TreeMap<>();
        for (Material material : Material.values()) {
            sortedMaterials.put(material.name(), 0.0);
        }
        FileConfiguration config = getConfig();
        for (Map.Entry<String, Double> entry : sortedMaterials.entrySet()) {
            config.set("items." + entry.getKey(), entry.getValue());
        }
        config.set("single-item", true);
        config.set("stack-item", false);
        saveConfig();
    }

    private boolean setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    public double getItemSellPrice(ItemStack item) {
        if (item == null) return 0;
        String materialName = item.getType().name();
        Double configPrice = configManager.getPrice(materialName);
        return (configPrice != null) ? configPrice : 0;
    }

    public String formatPrice(double price) {
        DecimalFormat df = new DecimalFormat("0.#");
        if (price < 1000) {
            return df.format(price);
        } else if (price < 1_000_000) {
            double value = price / 1000;
            return df.format(value) + "K";
        } else if (price < 1_000_000_000) {
            double value = price / 1_000_000;
            return df.format(value) + "M";
        } else if (price < 1_000_000_000_000L) {
            double value = price / 1_000_000_000;
            return df.format(value) + "B";
        } else {
            double value = price / 1_000_000_000_000L;
            return df.format(value) + "Tm";
        }
    }

    public void updateItemLore(ItemStack item) {
        if (item == null) return;
        double basePrice = getItemSellPrice(item);
        FileConfiguration config = getConfig();
        boolean stackItem = config.getBoolean("stack-item", false);
        double finalPrice = stackItem ? basePrice * item.getAmount() : basePrice;
        String priceStr = formatPrice(finalPrice);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = new ArrayList<>();
        lore.add("§7Worth: §a" + priceStr);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private void processConfigMaterials() {
        int count = 0;
        FileConfiguration config = getConfig();
        if (config.contains("items")) {
            for (String materialName : config.getConfigurationSection("items").getKeys(false)) {
                Material m = Material.getMaterial(materialName);
                if (m != null && m.isItem()) {
                    ItemStack dummyItem = new ItemStack(m);
                    updateItemLore(dummyItem);
                    count++;
                }
            }
        }
        getLogger().info("Processed " + count + " materials");
    }

    public void updateAllPlayerItemsForInventory(Inventory inv) {
        if (inv == null) return;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                updateItemLore(item);
            }
        }
    }

    public void updateItemsFromArray(ItemStack[] items) {
        if (items == null) return;
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                updateItemLore(item);
            }
        }
    }

    private void updateAllPlayerItems() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateAllPlayerItemsForInventory(player.getInventory());
            updateItemsFromArray(player.getInventory().getArmorContents());
        }
        getLogger().info("Updated lore for all online players' items.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("worthapi")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                configManager.reload(getConfig());
                sender.sendMessage(ChatColor.GREEN + "WorthAPI config reloaded!");
                updateAllPlayerItems();
                return true;
            }
        }
        return false;
    }
}
