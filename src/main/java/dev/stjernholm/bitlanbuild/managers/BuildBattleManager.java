package dev.stjernholm.bitlanbuild;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BuildBattleManager {
    private COMPETITION_STATUS status;
    private BitlanBuild instance;
    private BossBar statusBar;

    public BuildBattleManager(BitlanBuild instance) {
        this.instance = instance;
        this.statusBar = Bukkit.createBossBar("§eWaiting for the competition to start", BarColor.YELLOW, BarStyle.SOLID);
        statusBar.setVisible(true);
        this.status = COMPETITION_STATUS.WAITING;

        World world = Bukkit.getWorld("plotworld");
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion("__global__");
        region.setFlag(Flags.BUILD, StateFlag.State.DENY);
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
    }

    public enum COMPETITION_STATUS {
        WAITING,
        STARTED,
        ENDED;
    }

    public COMPETITION_STATUS getStatus() {
        return status;
    }
}
