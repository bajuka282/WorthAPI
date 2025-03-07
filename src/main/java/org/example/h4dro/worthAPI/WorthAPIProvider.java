package org.example.h4dro.worthAPI;

import org.bukkit.inventory.ItemStack;
import org.example.h4dro.worthAPI.WorthAPI;

public class WorthAPIProvider {
    public static double getItemSellPrice(ItemStack item) {
        return WorthAPI.getInstance().getItemSellPrice(item);
    }
}
