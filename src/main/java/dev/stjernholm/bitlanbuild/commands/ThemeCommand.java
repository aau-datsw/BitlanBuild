package dev.stjernholm.bitlanbuild.commands;

import dev.stjernholm.bitlanbuild.BitlanBuild;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ThemeCommand implements CommandExecutor {
    private BitlanBuild instance;

    public ThemeCommand(BitlanBuild instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)) return false;

        if(args.length > 0) {
            String theme = String.join(" ", args);
            this.instance.getBuildBattleManager().startWithTheme(theme);
        } else {
            this.instance.getBuildBattleManager().end();
        }
        return false;
    }
}
