package org.animey.auth;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class Listeners implements Listener {
    SqlStorage storage;

    public Listeners(SqlStorage storage) {
        this.storage = storage;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMessage(AsyncChatEvent e){
        if(storage.frozen.contains(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Component.text("Not logged in. Please log in or register.", Style.style(TextColor.color(200, 0, 0))));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e){
        if(storage.frozen.contains(e.getPlayer()) && (!e.getMessage().contains("/register") && !e.getMessage().contains("/login"))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Component.text("Not logged in. Please log in or register.", Style.style(TextColor.color(200, 0, 0))));
        }

    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e){
        if(storage.frozen.contains(e.getPlayer())) e.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent e){
        if(storage.frozen.contains(e.getPlayer())) e.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConnect(PlayerJoinEvent e){ // Called when player joins the event
        storage.frozen.add(e.getPlayer());
        if(storage.getPassword(e.getPlayer()).isEmpty()){ // register message
            e.getPlayer().sendMessage("Please register with /register <password>");
        } else { // login message
            e.getPlayer().sendMessage("Please login with /login <password>");
        }
    }
}
