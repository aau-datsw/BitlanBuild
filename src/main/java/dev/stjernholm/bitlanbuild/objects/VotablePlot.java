package dev.stjernholm.bitlanbuild.objects;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import dev.stjernholm.bitlanbuild.BitlanBuild;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class VotablePlot {

    private final UUID owner;
    private final HashSet<Vote> votes;
    private final BitlanBuild instance;

    public VotablePlot(Plot plot, ArrayList<Category> categories, BitlanBuild instance) {
        this.owner = plot.getOwner();
        this.votes = new HashSet<>();
        this.instance = instance;

        File plotFile = new File("plugins/BitlanBuild/plots", owner.toString() + ".yml");
        if (plotFile.exists()) {
            FileConfiguration plotConfig = YamlConfiguration.loadConfiguration(plotFile);
            if(!plotConfig.contains("votes")) return;
            plotConfig.getConfigurationSection("votes").getKeys(false).forEach(categoryID -> {
                if(categories.stream().anyMatch(category -> category.getUuid().equals(categoryID))) {
                    plotConfig.getConfigurationSection("votes." + categoryID).getKeys(false).forEach(playerID -> votes.add(new Vote(UUID.fromString(categoryID), UUID.fromString(playerID), plotConfig.getInt("votes." + categoryID + "." + playerID))));
                }
            });
        }
    }

    public boolean alreadyVotedAllCategories(Player player, ArrayList<Category> categories) {
        for (Category category : categories) {
            if(!alreadyCastedVoteInCategory(UUID.fromString(category.getUuid()), player).isPresent()) return false;
        }
        return true;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getRating(UUID categoryID, Player player) {
        Optional<Vote> vote = alreadyCastedVoteInCategory(categoryID, player);
        return vote.map(Vote::getRating).orElse(0);
    }

    public void castVote(UUID category, Player player, int rating) {
        Optional<Vote> vote = alreadyCastedVoteInCategory(category, player);
        if(vote.isPresent()) {
            player.sendMessage(Component.text("New rating for this category: " + rating).color(TextColor.color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue())));
            vote.get().setRating(rating);
            save();
        } else {
            player.sendMessage(Component.text("Set rating for this category to: " + rating).color(TextColor.color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue())));
            votes.add(new Vote(category, player.getUniqueId(), rating));
            save();
        }
    }

    public double calculateTotalScore() {
        Map<UUID, Double> scores = new HashMap<>();
        Map<UUID, Integer> total = new HashMap<>();
        for (Vote vote : votes) {
            scores.put(vote.getCategory(), scores.getOrDefault(vote.getCategory(), 0.0) + vote.getRating());
            total.put(vote.getCategory(), total.getOrDefault(vote.getCategory(), 0) + 1);
        }
        if(total.isEmpty()) return 0;

        return scores.keySet().stream().mapToDouble(category -> scores.get(category) / total.get(category)).sum();
    }

    public double categoryScore(UUID category) {
        Map<UUID, Double> categoryScore = new HashMap<>();
        int total = 0;
        for (Vote vote : votes) {
            if(vote.getCategory().equals(category)) {
                categoryScore.put(vote.getPlayer(), categoryScore.getOrDefault(vote.getPlayer(), 0.0) + vote.getRating());
                total++;
            }
        }

        if(total == 0) return 0;

        return categoryScore.values().stream().mapToDouble(Double::intValue).sum() / total;
    }

    private Optional<Vote> alreadyCastedVoteInCategory(UUID category, Player player) {
        return votes.stream().filter(vote -> vote.isMatch(category, player.getUniqueId())).findAny();
    }

    private void save() {
        File plotFile = new File("plugins/BitlanBuild/plots", owner.toString() + ".yml");
        FileConfiguration plotConfig = YamlConfiguration.loadConfiguration(plotFile);

        for(Vote vote : votes) {
            plotConfig.set("votes." + vote.getCategory().toString() + "." + vote.getPlayer().toString(), vote.getRating());
        }

        try {
            plotConfig.save(plotFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}