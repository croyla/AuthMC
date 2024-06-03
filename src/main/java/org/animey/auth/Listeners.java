package org.animey.auth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Listeners implements Listener {
    SqlStorage storage;

    public Listeners(SqlStorage storage) {
        this.storage = storage;
    }
    @EventHandler
    public void onPlayerMessage(){}
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(storage.frozen.contains(e.getPlayer())) e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if(storage.frozen.contains(e.getPlayer())) e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent e){ // Called when player joins the event
        storage.frozen.add(e.getPlayer());
        if(storage.getPassword(e.getPlayer()).isEmpty()){ // register message
            e.getPlayer().sendMessage("Please register with /register <password>");
        } else { // login message
            e.getPlayer().sendMessage("Please login with /login <password>");
        }
    }
}
