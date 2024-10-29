package dev.stjernholm.bitlanbuild.managers;

import com.plotsquared.core.PlotAPI;
import org.bukkit.Bukkit;

public class PlotManager {

    private PlotAPI plotAPI;

    public PlotManager() {
        if(Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {
            this.disable();
            Bukkit.getServer().shutdown();
        }
        this.plotAPI = new PlotAPI();
    }

    public void disable() {
        this.plotAPI = null;
    }

    public PlotAPI getPlotAPI() {
        return plotAPI;
    }
}
