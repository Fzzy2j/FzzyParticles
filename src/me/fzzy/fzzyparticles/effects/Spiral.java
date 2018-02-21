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

public class Spiral implements ParticleEffect {

    private Location last = null;
    private double yaw = 0;
    private Particle particle;
    private double hypotheticalCoord = 0;
    private double distance = 0;

    public Spiral(Particle particle) {
        this.particle = particle;
    }

    @Override
    public ParticleEffectType getType() {
        return ParticleEffectType.SPIRAL;
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
            distance = last.distanceSquared(loc);
            if (distance > 0.1) {
                yaw = -lookAt.getYaw();
            }
            double t = -((yaw + 90) * (Math.PI / 180));
            if (offset > 0)
                loc.add(Math.cos(t) * offset, 0, Math.sin(t) * offset);
            loc.add(0, 1, 0);

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

            ParticleP p = FzzyParticles.getPlayerParticle(player);

            hypotheticalCoord += last.distance(loc);
            if (hypotheticalCoord > Math.PI * 2)
                hypotheticalCoord -= Math.PI * 2;

            double zS = -(yaw * (Math.PI / 180));

            // zS = phi

            for (double i = 1; i <= density; i++) {
                double newMod = ((Math.PI * (double) 2) / (double) density) * i;
                Location display = loc.clone().add((Math.cos(zS) * (Math.sin(hypotheticalCoord + newMod)) * p.getPreset().getSize()), Math.cos(hypotheticalCoord + newMod) * p.getPreset().getSize(), (Math.sin(zS) * (Math.sin(hypotheticalCoord + newMod)) * p.getPreset().getSize()));

                ParticlePlayer.playParticle(p, particle, vel, display, 100);
            }
        }
        last = loc;
    }

}