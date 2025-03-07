package org.example.h4dro.worthAPI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUpdateListener implements Listener {

    // Khi người chơi mở bất kỳ inventory nào (bao gồm cả rương, creative, vv)
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        updateInventoryItems(event.getInventory());
    }

    // Khi người chơi click trong inventory
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Đợi 1 tick để chắc chắn rằng hành động click đã được xử lý
        Bukkit.getScheduler().runTask(WorthAPI.getInstance(), () -> {
            updateInventoryItems(event.getInventory());
        });
    }

    // Khi người chơi kéo item trong inventory
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Bukkit.getScheduler().runTask(WorthAPI.getInstance(), () -> {
            updateInventoryItems(event.getInventory());
        });
    }

    // Khi người chơi vừa join, cập nhật inventory của họ
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateInventoryItems(player.getInventory());
    }

    // Phương thức cập nhật toàn bộ item trong một inventory
    private void updateInventoryItems(Inventory inv) {
        if (inv == null) return;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                WorthAPI.getInstance().updateItemLore(item);
            }
        }
    }
}