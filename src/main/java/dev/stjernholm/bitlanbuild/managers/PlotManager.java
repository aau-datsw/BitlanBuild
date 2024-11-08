package dev.stjernholm.bitlanbuild.managers;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import dev.stjernholm.bitlanbuild.BitlanBuild;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class PlotManager {
    private final TextColor red = TextColor.color(228, 47, 49);

    private PlotAPI plotAPI;
    private BitlanBuild instance;

    public PlotManager(BitlanBuild instance) {
        if(Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {
            this.disable();
            Bukkit.getServer().shutdown();
        }
        this.instance = instance;
        this.plotAPI = new PlotAPI();
    }

    public void disable() {
        this.plotAPI = null;
    }

    public PlotAPI getPlotAPI() {
        return plotAPI;
    }

    public void teleportPlayer(Player player) {
        Optional<Plot> plot = getPlotAPI().getAllPlots().stream().filter(_plot -> _plot.getOwner().equals(player.getUniqueId()) || _plot.getTrusted().contains(player.getUniqueId())).findFirst();
        PlotPlayer<Player> teleportPlayer = PlotPlayer.from(player);
        if(!plot.isPresent()) {
            if(player.isOp()) {
                player.sendMessage(Component.text("Since you're an administrator no plot has been created for you.").color(red));
            } else {
                player.sendMessage(Component.text("You don't have a plot. Please try to rejoin the server! :)").color(red));
            }
        } else {
            plot.ifPresent(value -> value.teleportPlayer(teleportPlayer, couldTeleport -> {}));
        }
    }

    public void teleportPlayer(Player player, UUID plotOwner) {
        PlotPlayer<Player> teleportPlayer = PlotPlayer.from(player);
        Optional<Plot> plot = getPlotAPI().getAllPlots().stream().filter(_plot -> _plot.getOwner().equals(plotOwner)).findFirst();
        plot.ifPresent(value -> value.teleportPlayer(teleportPlayer, couldTeleport -> {
            if(!couldTeleport) {
                player.sendMessage(Component.text("Some error happened and therefore couldn't teleport you.").color(red));
            }
        }));
    }
}
