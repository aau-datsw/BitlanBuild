package dev.stjernholm.bitlanbuild.commands;

import dev.stjernholm.bitlanbuild.BitlanBuild;
import dev.stjernholm.bitlanbuild.managers.BuildBattleManager;
import dev.stjernholm.bitlanbuild.objects.Category;
import dev.stjernholm.bitlanbuild.objects.VotablePlot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ResultCommand implements CommandExecutor {
    private final BitlanBuild instance;

    public ResultCommand(BitlanBuild instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        if(instance.getBuildBattleManager().getStatus() != BuildBattleManager.COMPETITION_STATUS.ENDED) {
            player.sendMessage(Component.text("The competition is still ongoing, end the competition before proceeding.").color(TextColor.color(228, 47, 49)));
            return false;
        }

        List<VotablePlot> sorted = instance.getBuildBattleManager().getVoteablePlots().stream()
                .sorted(Comparator.comparingDouble((VotablePlot plot) -> plot.calculateTotalScore()).reversed())
                .collect(Collectors.toList());

        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&6BitLAN &e- &6BuildBattle results:"));
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&7-------------------------------------"));
        sorted.forEach(plot -> {
            double totalScore = plot.calculateTotalScore();
            Component ratingText = LegacyComponentSerializer.legacyAmpersand().deserialize("&8[&6" + totalScore + "&8] &e");
            Component hoverText = LegacyComponentSerializer.legacyAmpersand().deserialize("&6Plot: &e" + Bukkit.getOfflinePlayer(plot.getOwner()).getName());
            hoverText = hoverText.appendNewline();
            hoverText = hoverText.append(LegacyComponentSerializer.legacyAmpersand().deserialize("&7--------------------------"));
            hoverText = hoverText.appendNewline();
            hoverText = hoverText.append(LegacyComponentSerializer.legacyAmpersand().deserialize("&6- &2Total Score&6: &a" + totalScore));
            for(Category category : instance.getBuildBattleManager().getCategories()) {
                hoverText = hoverText.appendNewline();
                hoverText = hoverText.append(LegacyComponentSerializer.legacyAmpersand().deserialize("&6- &e" + category.getName() + "&6: &e" + plot.categoryScore(UUID.fromString(category.getUuid()))));
            }
            hoverText = hoverText.appendNewline();
            hoverText = hoverText.append(LegacyComponentSerializer.legacyAmpersand().deserialize("&7--------------------------"));
            ratingText = ratingText.hoverEvent(HoverEvent.showText(hoverText));
            Component gotoPlot = LegacyComponentSerializer.legacyAmpersand().deserialize(" &8[&6Teleport&8]");
            gotoPlot = gotoPlot.clickEvent(ClickEvent.runCommand("/p2 tp " + Bukkit.getOfflinePlayer(plot.getOwner()).getName()));
            player.sendMessage(ratingText.append(LegacyComponentSerializer.legacyAmpersand().deserialize("&e" + Bukkit.getOfflinePlayer(plot.getOwner()).getName())).append(gotoPlot));
        });
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&7-------------------------------------"));

        return false;
    }
}
