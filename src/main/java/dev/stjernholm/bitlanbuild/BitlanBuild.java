package dev.stjernholm.bitlanbuild;

import dev.stjernholm.bitlanbuild.commands.PlotCommand;
import dev.stjernholm.bitlanbuild.commands.ResultCommand;
import dev.stjernholm.bitlanbuild.commands.ThemeCommand;
import dev.stjernholm.bitlanbuild.commands.VoteCommand;
import dev.stjernholm.bitlanbuild.managers.BuildBattleManager;
import dev.stjernholm.bitlanbuild.managers.EventManager;
import dev.stjernholm.bitlanbuild.managers.PlotManager;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public final class BitlanBuild extends ExtendedJavaPlugin {

    private EventManager eventManager;
    private PlotManager plotManager;
    private BuildBattleManager buildBattleManager;

    @Override
    public void enable() {
        this.eventManager = new EventManager(this);
        this.plotManager = new PlotManager(this);
        this.buildBattleManager = new BuildBattleManager(this);
        registerCommands();
        getConfig().options().copyDefaults(true);
        saveConfig();


    }

    @Override
    public void disable() {
        buildBattleManager.disable();
        buildBattleManager = null;
        eventManager.disable();
        eventManager = null;
        plotManager.disable();
        plotManager = null;
    }

    public PlotManager getPlotManager() {
        return plotManager;
    }

    public BuildBattleManager getBuildBattleManager() {
        return buildBattleManager;
    }

    private void registerCommands() {
        getCommand("plot").setExecutor(new PlotCommand(this));
        getCommand("theme").setExecutor(new ThemeCommand(this));
        getCommand("vote").setExecutor(new VoteCommand(this));
        getCommand("result").setExecutor(new ResultCommand(this));
    }
}
