package me.fzzy.fzzyparticles.listeners;

import java.util.HashMap;
import java.util.UUID;

import me.fzzy.fzzyparticles.Flag;
import me.fzzy.fzzyparticles.FzzyParticles;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    public static HashMap<UUID, Long> timers;

    public PlayerMoveListener() {
        timers = new HashMap<>();
    }

    @EventHandler
    public void onParticlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().distanceSquared(event.getFrom()) > 0.01) {
            if (FzzyParticles.getPlayerParticle(player).getPreset().getFlags().contains(Flag.MOVING) || FzzyParticles.getPlayerParticle(player).getPreset().getFlags().contains(Flag.AFK)) {
                timers.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    public static boolean isPlayerAFK(Player player) {
        if (!timers.containsKey(player.getUniqueId()))
            timers.put(player.getUniqueId(), (long) 0);
        if (System.currentTimeMillis() - timers.get(player.getUniqueId()) > 1000)
            return true;
        else
            return false;
    }

    public static boolean isPlayerMoving(Player player) {
        if (!timers.containsKey(player.getUniqueId()))
            timers.put(player.getUniqueId(), (long) 0);
        if (System.currentTimeMillis() - timers.get(player.getUniqueId()) < 100)
            return true;
        else
            return false;
    }

}
