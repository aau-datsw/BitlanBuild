package dev.stjernholm.bitlanbuild.commands;

import dev.stjernholm.bitlanbuild.BitlanBuild;
import dev.stjernholm.bitlanbuild.managers.BuildBattleManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class VoteCommand implements CommandExecutor {
    private final BitlanBuild instance;

    public VoteCommand(BitlanBuild instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) return false;

        Player player = (Player)commandSender;

        if(instance.getBuildBattleManager().getStatus() != BuildBattleManager.COMPETITION_STATUS.ENDED) {
            player.sendMessage(Component.text("The competition is still ongoing, end the competition before proceeding.").color(TextColor.color(228, 47, 49)));
            return false;
        }

        if (instance.getBuildBattleManager().getVoteablePlots().isEmpty()) {
            player.sendMessage(Component.text("No votable plots found.").color(TextColor.color(228, 47, 49)));
            return false;
        }

        player.getInventory().clear();

        instance.getBuildBattleManager().getCategories().forEach(category -> player.getInventory().addItem(category.itemStack));
        ItemStack unvisitedPlot = new ItemStack(Material.END_PORTAL_FRAME);
        ItemMeta unvisitedMeta = unvisitedPlot.getItemMeta();
        unvisitedMeta.displayName(Component.text("Teleport to next votable plot").color(TextColor.color(255, 79, 76)));
        unvisitedPlot.setItemMeta(unvisitedMeta);
        player.getInventory().setItem(8, unvisitedPlot);

        return false;
    }
}
