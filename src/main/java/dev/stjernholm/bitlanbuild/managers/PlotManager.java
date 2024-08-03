package dev.stjernholm.bitlanbuild.managers;

import com.plotsquared.core.PlotAPI;
import dev.stjernholm.bitlanbuild.BitlanBuild;
import org.bukkit.Bukkit;

public class PlotManager {

    private BitlanBuild instance;
    private PlotAPI plotAPI;

    public PlotManager(BitlanBuild instance) {
        this.instance = instance;
        if(Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {
            this.disable();
            Bukkit.getServer().shutdown();
        }
        this.plotAPI = new PlotAPI();
    }

    public void disable() {
        this.plotAPI = null;
        this.instance = null;
    }

    public PlotAPI getPlotAPI() {
        return plotAPI;
    }
}
