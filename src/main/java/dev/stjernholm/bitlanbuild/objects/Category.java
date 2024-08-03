package dev.stjernholm.bitlanbuild.objects;

import dev.stjernholm.bitlanbuild.BitlanBuild;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Category {

    private final String name;
    public final ItemStack itemStack;
    private final String uuid;

    public Category(String name, Material item, String uuid, BitlanBuild instance) {
        this.name = name;
        this.uuid = uuid;

        ItemStack categoryItem = new ItemStack(item);
        ItemMeta categoryItemMeta = categoryItem.getItemMeta();
        categoryItemMeta.displayName(Component.text(name.replace("&", "§")));

        NamespacedKey key = new NamespacedKey(instance, "vote-category");

        categoryItemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, uuid);
        categoryItem.setItemMeta(categoryItemMeta);
        this.itemStack = categoryItem;
    }
}
