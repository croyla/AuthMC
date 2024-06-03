package org.animey.auth;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
    SqlStorage storage;
    public Commands(SqlStorage storage){
        this.storage = storage;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (label) {
            case "register":
                if (args.length != 1) {
                    commandSender.sendMessage(Component.text("Found more than one argument.", Style.style(TextColor.color(200, 0, 0))));
                    return false;
                }
                if (!(commandSender instanceof Player)){
                    commandSender.sendMessage(Component.text("Sender must be player.", Style.style(TextColor.color(200, 0, 0))));
                    return true;
                }
                if (!storage.frozen.contains((Player) commandSender)) {
                    commandSender.sendMessage(Component.text("Already logged in.", Style.style(TextColor.color(200, 0, 0))));
                    return true;
                }
                if (storage.setPassword((Player) commandSender, args[0])) {
                    storage.frozen.remove((Player) commandSender);
                    commandSender.sendMessage(Component.text("Registered", Style.style(TextColor.color(0, 200, 0))));
                    return true;
                }
                commandSender.sendMessage(Component.text("Error.", Style.style(TextColor.color(200, 0, 0))));
                return true;
            case "login":
                if (args.length != 1) {
                    commandSender.sendMessage(Component.text("Found more than one argument.", Style.style(TextColor.color(200, 0, 0))));
                    return false;

                }
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(Component.text("Sender must be player.", Style.style(TextColor.color(200, 0, 0))));
                    return true;
                }
                if (!storage.frozen.contains((Player) commandSender)) {
                    commandSender.sendMessage(Component.text("Already logged in.", Style.style(TextColor.color(200, 0, 0))));
                    return true;
                }
                if (storage.verifyPassword((Player) commandSender, args[0])) {
                    storage.frozen.remove(commandSender);
                    commandSender.sendMessage(Component.text("Logged in", Style.style(TextColor.color(0, 200, 0))));
                    return true;
                } else {
                    commandSender.sendMessage(Component.text("Incorrect password.", Style.style(TextColor.color(200, 0, 0))));
                    return true;
                }

            case "changepw":
                if (!(commandSender instanceof Player)) return false;
                if (storage.frozen.contains((Player) commandSender)) {
                    commandSender.sendMessage(Component.text("Not logged in.", Style.style(TextColor.color(200, 0, 0))));
                    return true;
                }
                if (args.length != 2) {
                    commandSender.sendMessage(Component.text("Incorrect argument count.", Style.style(TextColor.color(200, 0, 0))));
                    return false;
                }
                if(!storage.verifyPassword((Player) commandSender, args[0])){
                    commandSender.sendMessage(Component.text("Incorrect password.", Style.style(TextColor.color(200, 0, 0))));
                    return true;
                }
                return storage.changePassword((Player) commandSender, args[0], args[1]);
            case "reset":
                if (commandSender instanceof Player) return false;
                if (args.length != 1) return false;
                if (Bukkit.getPlayer(args[0]) != null) {
                    if (storage.deletePassword(args[0])) {
                        if(Bukkit.getPlayer(args[0]).isOnline()) Bukkit.getPlayer(args[0]).kick(Component.text("Password Reset, please reconnect."));
                        commandSender.sendMessage(Component.text("Reset player password", Style.style(TextColor.color(0, 200, 0))));
                        return true;
                    } else commandSender.sendMessage(Component.text("Failed reset in DB.", Style.style(TextColor.color(200, 0, 0))));
                } else commandSender.sendMessage(Component.text("Player not found.", Style.style(TextColor.color(200, 0, 0))));
                return false;
            default:
                return false;
        }
    }
}
