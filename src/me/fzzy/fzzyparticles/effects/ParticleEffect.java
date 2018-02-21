package me.fzzy.fzzyparticles.effects;

import java.util.ArrayList;

import me.fzzy.fzzyparticles.Flag;
import me.fzzy.fzzyparticles.ParticleEffectType;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface ParticleEffect {

    void setParticle(Particle particle);

    Particle getParticle();

    ParticleEffectType getType();

    void run(Player player, Vector vel, ArrayList<Flag> flags, int density, double offset);

}
