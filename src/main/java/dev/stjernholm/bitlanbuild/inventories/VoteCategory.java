package dev.stjernholm.bitlanbuild.inventories;

import dev.stjernholm.bitlanbuild.objects.Category;
import dev.stjernholm.bitlanbuild.objects.VotablePlot;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class VoteCategory extends Gui {

    private final ItemStack itemStack;
    private final VotablePlot votablePlot;
    private final Category category;
    private int rating;
    private boolean ratingChanged = false;

    private static final MenuScheme RATINGS = new MenuScheme()
            .mask("000000000")
            .mask("001111100")
            .mask("000000000");

    private static final MenuScheme CURRENTLY_SELECTED = new MenuScheme()
            .mask("001111100")
            .mask("000000000")
            .mask("001111100");

    public VoteCategory(Player player, Category category, VotablePlot votablePlot) {
        super(player, 3, category.getName());
        this.itemStack = category.itemStack;
        this.category = category;
        this.votablePlot = votablePlot;
        this.rating = votablePlot.getRating(UUID.fromString(category.getUuid()), player);
    }

    @Override
    public void redraw() {
        if (isFirstDraw()) {
            bind(() -> {
                if(rating == 0 || !ratingChanged) return;
                votablePlot.castVote(UUID.fromString(category.getUuid()), getPlayer(), rating);
            });
        }

        MenuPopulator populator = RATINGS.newPopulator(this);
        for(int i = 1; i <= 5; i++) {
            ItemStackBuilder itemStackBuilder = ItemStackBuilder.of(itemStack.clone()).lore("&eSæt rating til: &6" + i);
            itemStackBuilder.amount(i);
            if(rating == i) {
                itemStackBuilder.enchant(Enchantment.UNBREAKING, i);
                itemStackBuilder.flag(ItemFlag.HIDE_ENCHANTS);
            }
            int itemRating = i;
            populator.acceptIfSpace(itemStackBuilder.build(() -> {
                rating = itemRating;
                ratingChanged = true;
                redraw();
            }));
        }
        if(rating != 0) {
            MenuPopulator selected = CURRENTLY_SELECTED.newPopulator(this);
            for(int i = 1; i <= 10; i++) {
                if (i % 5 == rating % 5) {
                    selected.acceptIfSpace(ItemStackBuilder.of(Material.GREEN_DYE).name("&a* SELECTED *").buildItem().build());
                } else {
                    selected.acceptIfSpace(ItemStackBuilder.of(Material.AIR).buildItem().build());
                }
            }

        }

    }
}
