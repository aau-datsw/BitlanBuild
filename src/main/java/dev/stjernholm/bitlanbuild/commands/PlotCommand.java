package dev.stjernholm.bitlanbuild.commands;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import dev.stjernholm.bitlanbuild.BitlanBuild;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlotCommand implements CommandExecutor {
    private final TextColor grey = TextColor.color(63, 76, 72);
    private final TextColor red = TextColor.color(228, 47, 49);
    private final BitlanBuild instance;

    public PlotCommand(BitlanBuild instance) {
        this.instance = instance;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;
        PlotPlayer plotPlayer = PlotPlayer.from(player);

        if(plotPlayer.getPlotCount() > 0) {
            Plot plot = (Plot) plotPlayer.getPlots().iterator().next();
            plot.teleportPlayer(plotPlayer, couldTeleport -> {});
        } else {
            if(player.isOp()) {
                player.sendMessage(Component.text("Since you're an administrator no plot can be created for you.").color(red));
            } else {
                player.sendMessage(Component.text("You don't have a plot. Please try to rejoin the server! :)").color(red));
            }
        }

        return false;
    }
}
