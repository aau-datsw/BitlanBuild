package dev.stjernholm.bitlanbuild.managers;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import dev.stjernholm.bitlanbuild.BitlanBuild;
import dev.stjernholm.bitlanbuild.inventories.VoteCategory;
import dev.stjernholm.bitlanbuild.objects.Category;
import dev.stjernholm.bitlanbuild.objects.VotablePlot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class EventManager implements Listener {

    private BitlanBuild instance;
    private final TextColor grey = TextColor.color(63, 76, 72);

    public EventManager(BitlanBuild instance) {
        this.instance = instance;
        Bukkit.getPluginManager().registerEvents(this, this.instance);
    }

    public void disable() {
        this.instance = null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TextColor green = TextColor.color(119, 228, 70);
        event.joinMessage(
                Component.text("[").color(grey)
                        .append(Component.text("+").color(green))
                        .append(Component.text("] ").color(grey))
                        .append(Component.text(player.getName()).color(green))
        );

        this.instance.getBuildBattleManager().getStatusBar().addPlayer(player);

        // Get player as plot player type
        PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);

        //Count of plots player is member of:
        long plotCount = instance.getPlotManager().getPlotAPI().getAllPlots().stream().filter(_plot -> _plot.getOwner().equals(player.getUniqueId()) || _plot.getTrusted().contains(player.getUniqueId())).count();

        // Create new plot if no plot is created yet and player is not op.
        if(plotCount == 0 && !player.isOp() && instance.getBuildBattleManager().getStatus() != BuildBattleManager.COMPETITION_STATUS.ENDED) {
            player.sendMessage(Component.text("No current plot found, creating new plot").color(grey));
            // Get next available plot from center 0,0 plot id.
            Plot freePlot = plotPlayer.getApplicablePlotArea().getNextFreePlot(plotPlayer, PlotId.of(0,0));
            // Claim plot for player.
            freePlot.claim(plotPlayer, true,null, true,true);
            freePlot.teleportPlayer(plotPlayer, couldTeleport -> {});
        }

        Bukkit.getAsyncScheduler().runDelayed(instance, scheduledTask ->
                player.sendMessage(Component.text("To get to your plot at any time, execute the command ")
                .color(TextColor.color(255, 173, 0))
                .append(Component.text("/plot").color(TextColor.color(255, 79, 76)))), 1, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TextColor red = TextColor.color(228, 47, 49);
        event.quitMessage(
                Component.text("[").color(grey)
                        .append(Component.text("+").color(red))
                        .append(Component.text("] ").color(grey))
                        .append(Component.text(player.getName()).color(red))
        );
    }

    @EventHandler
    public void onRightClickItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.hasPermission("bitlan.op")) return;

        if((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && !player.getInventory().getItemInMainHand().isEmpty()
                && instance.getBuildBattleManager().getStatus() == BuildBattleManager.COMPETITION_STATUS.ENDED) {

            if(player.getInventory().getItemInMainHand().getType().equals(Material.END_PORTAL_FRAME)) {
                Optional<VotablePlot> missingPlot = instance.getBuildBattleManager().getVoteablePlots().stream().filter(votablePlot -> !votablePlot.alreadyVotedAllCategories(player, instance.getBuildBattleManager().getCategories())).findFirst();
                if(missingPlot.isPresent()) {
                    instance.getPlotManager().teleportPlayer(player, missingPlot.get().getOwner());
                    player.sendMessage(Component.text("Du mangler at afgive én eller flere votes på dette plot").color(TextColor.color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue())));
                } else {
                    player.sendMessage(Component.text("Der er ikke flere votable plots!").color(TextColor.color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue())));
                }
                return;
            }

            NamespacedKey key = new NamespacedKey(instance, "vote-category");
            if(player.getInventory().getItemInMainHand().getPersistentDataContainer().has(key)) {
                event.setCancelled(true);
                PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);
                Plot currentPlot = plotPlayer.getCurrentPlot();
                if (Objects.isNull(currentPlot) || !currentPlot.hasOwner()) return;
                String categoryID = player.getInventory().getItemInMainHand().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                Category category = instance.getBuildBattleManager().getCategories().stream().filter(category1 -> category1.getUuid().equals(categoryID)).findFirst().get();
                VotablePlot votablePlot = instance.getBuildBattleManager().getVoteablePlots().stream().filter(_votablePlot -> _votablePlot.getOwner().equals(currentPlot.getOwner())).findFirst().get();
                new VoteCategory(player, category, votablePlot).open();
            }
        }
    }

}
