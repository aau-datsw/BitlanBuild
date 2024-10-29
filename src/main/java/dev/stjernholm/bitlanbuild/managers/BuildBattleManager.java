package dev.stjernholm.bitlanbuild.managers;

import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.stjernholm.bitlanbuild.BitlanBuild;
import dev.stjernholm.bitlanbuild.objects.Category;
import dev.stjernholm.bitlanbuild.objects.VotablePlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class BuildBattleManager {
    private COMPETITION_STATUS status;
    private BitlanBuild instance;
    private BossBar statusBar;
    private final ArrayList<Category> categories;
    private final ArrayList<VotablePlot> voteablePlots;

    public BuildBattleManager(BitlanBuild instance) {
        this.instance = instance;
        this.statusBar = Bukkit.createBossBar("§eWaiting for the competition to start", BarColor.YELLOW, BarStyle.SOLID);
        statusBar.setVisible(true);
        this.status = COMPETITION_STATUS.WAITING;
        this.categories = new ArrayList<>();
        this.voteablePlots = new ArrayList<>();

        World world = Bukkit.getWorld("plotworld");
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion("__global__");
        region.setFlag(Flags.BUILD, StateFlag.State.DENY);

        loadCategories();
    }

    private void loadCategories() {
        FileConfiguration config = instance.getConfig();
        config.getConfigurationSection("categories").getKeys(false).forEach(category -> {
           instance.getLogger().info("Loading category: " + category);
           if(!config.contains("categories." + category + ".uuid")) {
               config.set("categories." + category + ".uuid", UUID.randomUUID().toString());
           }
           if(config.contains("categories." + category + ".name") && config.contains("categories." + category + ".item")) {
               Material material = Material.getMaterial(config.getString("categories." + category + ".item", "Category material not found"));
               String displayName = config.getString("categories." + category + ".name", "Category item name not found");
               String uuid = config.getString("categories." + category + ".uuid", "Category uuid not found");
               Category newCategory = new Category(displayName, material, uuid, instance);
               categories.add(newCategory);
           }
        });
        instance.saveConfig();
    }

    public void disable() {
        this.statusBar = null;
        this.instance = null;
    }

    public void startWithTheme(String theme) {
        if(statusBar != null) statusBar.removeAll();
        statusBar = Bukkit.createBossBar("§aTHEME: " + theme, BarColor.GREEN, BarStyle.SOLID);
        statusBar.setVisible(true);
        for(Player player : Bukkit.getOnlinePlayers()) {
            statusBar.addPlayer(player);
        }
        status = COMPETITION_STATUS.STARTED;

        World world = Bukkit.getWorld("plotworld");
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion("__global__");
        region.setFlag(Flags.BUILD, null);
    }

    public BossBar getStatusBar() {
        return statusBar;
    }

    public void end() {
        if(statusBar != null) statusBar.removeAll();
        statusBar = Bukkit.createBossBar("§cThe competition is now finished!", BarColor.RED, BarStyle.SOLID);
        statusBar.setVisible(true);
        for(Player player : Bukkit.getOnlinePlayers()) {
            statusBar.addPlayer(player);
        }
        status = COMPETITION_STATUS.ENDED;
        
        World world = Bukkit.getWorld("plotworld");
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion("__global__");
        region.setFlag(Flags.BUILD, StateFlag.State.DENY);

        voteablePlots.clear();

        for(Plot plot : instance.getPlotManager().getPlotAPI().getAllPlots()) {
            voteablePlots.add(new VotablePlot(plot, getCategories(), instance));
        }
    }

    public enum COMPETITION_STATUS {
        WAITING,
        STARTED,
        ENDED
    }

    public COMPETITION_STATUS getStatus() {
        return status;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public ArrayList<VotablePlot> getVoteablePlots() {
        return voteablePlots;
    }
}
