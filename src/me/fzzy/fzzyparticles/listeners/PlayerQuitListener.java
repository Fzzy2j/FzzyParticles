package me.fzzy.fzzyparticles.listeners;

import me.fzzy.fzzyparticles.FzzyParticles;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onParticlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FzzyParticles.getPlayerParticle(player).unload();
    }

}
