package me.fzzy.fzzyparticles.util;

import java.util.ArrayList;

import me.fzzy.fzzyparticles.Flag;
import me.fzzy.fzzyparticles.ParticleP;
import me.fzzy.fzzyparticles.listeners.PlayerMoveListener;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticlePlayer {

    public static void playParticle(ParticleP p, Particle effect, Vector vel, Location loc, int range) {
        if (effect.equals(Particle.WATER_BUBBLE)) {
            if (loc.getBlock().getType() != Material.STATIONARY_WATER && loc.getBlock().getType() != Material.WATER)
                return;
        }
        Player player = Bukkit.getPlayer(p.getPlayer());
        boolean isOnGround = player.getLocation().clone().subtract(0, 0.1, 0).getBlock().getType().isSolid();
        boolean isAFK = PlayerMoveListener.isPlayerAFK(player);
        GameMode gamemode = player.getGameMode();
        boolean isMoving = PlayerMoveListener.isPlayerMoving(player);

        ArrayList<Flag> flags = p.getPreset().getFlags();
        if (!flags.contains(Flag.NONE)) {
            if (flags.contains(Flag.ADVENTURE) && gamemode == GameMode.ADVENTURE)
                return;
            if (flags.contains(Flag.CREATIVE) && gamemode == GameMode.CREATIVE)
                return;
            if (flags.contains(Flag.SURVIVAL) && gamemode == GameMode.SURVIVAL)
                return;
            if (flags.contains(Flag.MOVING) && !isMoving)
                return;
            if (flags.contains(Flag.ON_GROUND) && !isOnGround)
                return;
            if (flags.contains(Flag.AFK) && !isAFK)
                return;
        }

        Bukkit.getPlayer(p.getPlayer()).getWorld().spawnParticle(effect, loc, 0, vel.getX(), vel.getY(), vel.getZ());
    }

    public static void playParticle(Particle effect, Vector vel, Location loc, int range) {
        if (effect.equals(Particle.WATER_BUBBLE)) {
            if (loc.getBlock().getType() != Material.STATIONARY_WATER && loc.getBlock().getType() != Material.WATER)
                return;
        }
        loc.getWorld().spawnParticle(effect, loc, 0, vel.getX(), vel.getY(), vel.getZ());
    }

}
