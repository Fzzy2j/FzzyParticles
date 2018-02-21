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

public class Line implements ParticleEffect {

    private Location last = null;
    private double yaw = 0;
    private Particle particle;

    public Line(Particle particle) {
        this.particle = particle;
    }

    @Override
    public ParticleEffectType getType() {
        return ParticleEffectType.LINE;
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
            if (offset > 0)
                loc.add(Math.cos(t) * offset, 0, Math.sin(t) * offset);

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

            Location display = loc.clone().add(0, p.getPreset().getSize(), 0);
            for (int i = 0; i <= density; i++) {
                double newT = -((yaw) * (Math.PI / 180));
                double x = Math.cos(newT) * ((double) (i * 2) / (double) 10);
                double z = Math.sin(newT) * ((double) (i * 2) / (double) 10);

                ParticlePlayer.playParticle(p, particle, vel, display.clone().add(x, 0, z), 100);
                if (i != 0)
                    ParticlePlayer.playParticle(p, particle, vel, display.clone().add(-x, 0, -z), 100);
            }
        }
        last = loc;
    }

}
