package me.jetao.options.listeners;

import me.jetao.options.OptionsPlugin;
import me.jetao.options.hologram.Hologram;
import me.jetao.options.hologram.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class HologramListeners implements Listener {

    public HologramListeners() {
        Bukkit.getPluginManager().registerEvents(this, OptionsPlugin.getInstance());
    }

    @EventHandler
    public void onPlayerScroll(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (!OptionsPlugin.getHologramManager().hasHologram(player)) {
            return;
        }
        Hologram hologram = OptionsPlugin.getHologramManager().getHologram(player);
        int previousSlot = event.getPreviousSlot();
        int newSlot = event.getNewSlot();
        if (previousSlot == 8 && newSlot == 0) {
            newSlot = 9;
        } else if (previousSlot == 0 && newSlot == 8) {
            previousSlot = 9;
        }
        int direction = previousSlot < newSlot ? 1 : -1;
        hologram.scroll(direction);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!OptionsPlugin.getHologramManager().hasHologram(player)) {
            return;
        }
        event.setCancelled(true);
        Hologram hologram = OptionsPlugin.getHologramManager().getHologram(player);
        Response response = hologram.getCurrentOption().getResponse();
        if (response == null) {
            return;
        }
        response.callback(player, hologram);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getX() == event.getFrom().getX() && event.getTo().getZ() == event.getFrom().getZ()
            && event.getTo().getY() == event.getFrom().getY()) {
            return;
        }
        if (!OptionsPlugin.getHologramManager().hasHologram(player)) {
            return;
        }
        player.teleport(event.getFrom());
    }

}
