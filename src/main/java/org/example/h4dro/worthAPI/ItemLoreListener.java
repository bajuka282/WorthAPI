package org.example.h4dro.worthAPI;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ItemLoreListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        WorthAPI.getInstance().updateAllPlayerItemsForInventory(player.getInventory());
        WorthAPI.getInstance().updateItemsFromArray(player.getInventory().getArmorContents());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        WorthAPI.getInstance().updateAllPlayerItemsForInventory(event.getInventory());
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item itemEntity = event.getEntity();
        if (itemEntity != null) {
            ItemStack itemStack = itemEntity.getItemStack();
            WorthAPI.getInstance().updateItemLore(itemStack);
            itemEntity.setItemStack(itemStack);
        }
    }
}
