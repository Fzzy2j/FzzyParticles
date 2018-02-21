package me.fzzy.fzzyparticles.effects;

import java.util.ArrayList;

import me.fzzy.fzzyparticles.Flag;
import me.fzzy.fzzyparticles.FzzyParticles;
import me.fzzy.fzzyparticles.ParticleEffectType;
import me.fzzy.fzzyparticles.ParticleP;
import me.fzzy.fzzyparticles.util.Distance;
import me.fzzy.fzzyparticles.util.ParticlePlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Rings implements ParticleEffect {

    private Location last = null;
    private double yaw = 0;
    private Particle particle;

    public Rings(Particle particle) {
        this.particle = particle;
    }

    @Override
    public ParticleEffectType getType() {
        return ParticleEffectType.RINGS;
    }

    @Override
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    @Override
    public Particle getParticle() {
        return particle;
    }

    @Override
    public void run(Player player, Vector vel, ArrayList<Flag> flags, int density, double offset) {
        Location loc = player.getLocation();
        if (last == null)
            last = loc;
        if (last.getWorld().equals(loc.getWorld())) {
            Location lookAt = Distance.lookAt(last, loc);
            if (last.distanceSquared(loc) > 0.1) {
                yaw = -lookAt.getYaw();
            }
            double t = -((yaw + 90) * (Math.PI / 180));
            ParticleP p = FzzyParticles.getPlayerParticle(player);
            if (offset > 0)
                loc.add(Math.cos(t) * offset, 0, Math.sin(t) * offset);
            loc.add(0, p.getPreset().getSize(), 0);

            double velTLR = -((yaw - 180) * (Math.PI / 180));
            double velTFB = -((yaw - 90) * (Math.PI / 180));
            double LRX = Math.cos(velTLR) * vel.getX();
            double LRZ = Math.sin(velTLR) * vel.getX();
            double FBX = Math.cos(velTFB) * vel.getZ();
            double FBZ = Math.sin(velTFB) * vel.getZ();

            Vector repoVel = vel.clone();
            repoVel.setX(LRX + FBX);
            repoVel.setZ(LRZ + FBZ);
            vel = repoVel;

            double locX = loc.getX();
            double locZ = loc.getZ();
            double velX = vel.getX() + locX;
            double velZ = vel.getZ() + locZ;
            double distance = Math.pow(locX - velX, 2) + Math.pow(locZ - velZ, 2);
            double r = Math.abs(distance);
            for (double i = 0; i < 360; i += ((double) 180 / (double) density)) {
                double newT = (i * (Math.PI / 180));
                double x = Math.cos(newT) * r;
                double z = Math.sin(newT) * r;
                ParticlePlayer.playParticle(p, particle, new Vector(x, vel.getY(), z), loc, 100);
            }
        }
        last = loc;
    }

}
