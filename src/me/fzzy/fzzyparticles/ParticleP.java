package me.fzzy.fzzyparticles;

import java.util.ArrayList;
import java.util.UUID;

import me.fzzy.fzzyparticles.effects.*;
import me.fzzy.fzzyparticles.listeners.PlayerMoveListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ParticleP {

    private UUID player;

    private SavedPreset effect;

    public ParticleP(UUID player, SavedPreset effect) {
        this.player = player;
        this.effect = effect;
    }

    public ParticleP(UUID player) {
        this.player = player;
        this.effect = new SavedPreset("Custom", null, new Vector(0, 0, 0), new ArrayList<Flag>(), 0, 1, 0, 1, new ItemStack(Material.WATER_BUCKET));
    }

    public void run() {
        effect.getEffect().run(Bukkit.getPlayer(player), effect.getVector(), effect.getFlags(), effect.getDensity(), effect.getOffset());
    }

    public void setPreset(SavedPreset effect) {
        this.effect = effect;
    }

    public SavedPreset getPreset() {
        return effect;
    }

    public UUID getPlayer() {
        return player;
    }

    public ParticleP clone() {
        this.effect = effect.getCopy();
        return this;
    }

    public void setParticle(Particle particle) {
        this.effect.setParticle(particle);
    }

    public Particle getParticle() {
        if (this.effect != null)
            return this.effect.getParticle();
        else
            return Particle.FIREWORKS_SPARK;
    }

    public void setEffectType(ParticleEffectType type) {
        switch (type) {
            case LINE:
                effect.setEffect(new Line(getParticle()));
                break;
            case RINGS:
                effect.setEffect(new Rings(getParticle()));
                break;
            case ROSE:
                effect.setEffect(new Rose(getParticle()));
                break;
            case SPIRAL:
                effect.setEffect(new Spiral(getParticle()));
                break;
            case WALL:
                effect.setEffect(new Wall(getParticle()));
                break;
            case WAVE:
                effect.setEffect(new Wave(getParticle()));
                break;
            case NONE:
                effect.setEffect(null);
                effect.setName("None");
            default:
                effect.setEffect(null);
                break;
        }
    }

    public void makeCustom() {
        this.effect = effect.getCopy();
        this.effect.setName("Custom");
    }

    public ParticleEffectType getEffectType() {
        return this.effect.getEffect().getType();
    }

    @SuppressWarnings("unchecked")
    public void load(Player player) {
        ParticleEffectType effect = FzzyParticles.DEFAULT_PARTICLE;
        Vector vector = new Vector(0, 0, 0);
        ArrayList<Flag> flags = new ArrayList<Flag>();
        String yamlEffect = FzzyParticles.yaml.getString(player.getUniqueId() + ".effect") + "";
        int interval = 1;
        int density = 1;
        double size = 1;
        String presetName = "";
        double offset = 0;
        Particle particle = Particle.FIREWORKS_SPARK;

        for (ParticleEffectType e : ParticleEffectType.values()) {
            if (yamlEffect.equals(e.name())) {
                effect = ParticleEffectType.valueOf(yamlEffect);
            }
        }

        if (FzzyParticles.yaml.contains(player.getUniqueId() + ".vector")) {
            String[] values = FzzyParticles.yaml.getString(player.getUniqueId() + ".vector").split(",");
            double x = Double.parseDouble(values[0]);
            double y = Double.parseDouble(values[1]);
            double z = Double.parseDouble(values[2]);
            vector = new Vector(x, y, z);
        }

        if (FzzyParticles.yaml.contains(player.getUniqueId() + ".flags")) {
            ArrayList<String> sFlags = (ArrayList<String>) FzzyParticles.yaml.get(player.getUniqueId() + ".flags");
            ArrayList<Flag> nFlags = new ArrayList<>();
            for (String s : sFlags) {
                nFlags.add(Flag.valueOf(s));
            }
            flags = nFlags;
        }

        if (FzzyParticles.yaml.contains(player.getUniqueId() + ".interval")) {
            interval = FzzyParticles.yaml.getInteger(player.getUniqueId() + ".interval");
        }

        if (FzzyParticles.yaml.contains(player.getUniqueId() + ".density")) {
            density = FzzyParticles.yaml.getInteger(player.getUniqueId() + ".density");
        }

        if (FzzyParticles.yaml.contains(player.getUniqueId() + ".offset")) {
            offset = FzzyParticles.yaml.getDouble(player.getUniqueId() + ".offset");
        }

        if (FzzyParticles.yaml.contains(player.getUniqueId() + ".particle")) {
            particle = Particle.valueOf(FzzyParticles.yaml.getString(player.getUniqueId() + ".particle"));
        }

        if (FzzyParticles.yaml.contains(player.getUniqueId() + ".size")) {
            size = FzzyParticles.yaml.getDouble(player.getUniqueId() + ".size");
        }

        if (FzzyParticles.yaml.contains(player.getUniqueId() + ".preset")) {
            presetName = FzzyParticles.yaml.getString(player.getUniqueId() + ".preset");
        }

        switch (effect) {
            case LINE:
                this.effect.setEffect(new Line(particle));
                break;
            case RINGS:
                this.effect.setEffect(new Rings(particle));
                break;
            case ROSE:
                this.effect.setEffect(new Rose(particle));
                break;
            case SPIRAL:
                this.effect.setEffect(new Spiral(particle));
                break;
            case WALL:
                this.effect.setEffect(new Wall(particle));
                break;
            case WAVE:
                this.effect.setEffect(new Wave(particle));
                break;
            default:
                break;
        }

        this.effect.setFlags(flags);
        this.effect.setInterval(interval);
        this.effect.setVector(vector);
        this.effect.setDensity(density);
        this.effect.setOffset(offset);
        this.effect.setSize(size);

        for (SavedPreset sets : FzzyParticles.presets) {
            if (!presetName.equals("Custom")) {
                if (sets.getName().equals(presetName)) {
                    this.effect = new SavedPreset(presetName);
                }
            }
        }

        this.player = player.getUniqueId();
        if (!FzzyParticles.players.contains(this))
            FzzyParticles.players.add(this);
    }

    public void unload() {
        if (this.effect.getEffect() != null)
            FzzyParticles.yaml.set(player + ".effect", this.effect.getEffect().getType().name());
        else
            FzzyParticles.yaml.set(player + ".effect", ParticleEffectType.NONE.name());
        double x = effect.getVector().getX();
        double y = effect.getVector().getY();
        double z = effect.getVector().getZ();
        FzzyParticles.yaml.set(player + ".vector", x + "," + y + "," + z);
        ArrayList<String> newFlags = new ArrayList<String>();
        for (Flag flag : effect.getFlags()) {
            newFlags.add(flag.name());
        }
        FzzyParticles.yaml.set(player + ".flags", newFlags);
        FzzyParticles.yaml.set(player + ".preset", this.effect.getName());
        FzzyParticles.yaml.set(player + ".interval", effect.getInterval());
        FzzyParticles.yaml.set(player + ".density", effect.getDensity());
        FzzyParticles.yaml.set(player + ".offset", effect.getOffset());

        if (getParticle() != null)
            FzzyParticles.yaml.set(player + ".particle", getParticle().name());
        else
            FzzyParticles.yaml.set(player + ".particle", Particle.FIREWORKS_SPARK);

        FzzyParticles.yaml.set(player + ".size", effect.getSize());
        FzzyParticles.yaml.save();
        FzzyParticles.players.remove(this);
        PlayerMoveListener.timers.remove(player);
        Timer.intervals.remove(player);
    }

    public void unloadNoSave(FzzyParticles plugin) {
        if (this.effect.getEffect() != null)
            plugin.yaml.set(player + ".effect", this.effect.getEffect().getType().name());
        else
            plugin.yaml.set(player + ".effect", ParticleEffectType.NONE.name());
        double x = effect.getVector().getX();
        double y = effect.getVector().getY();
        double z = effect.getVector().getZ();
        plugin.yaml.set(player + ".vector", x + "," + y + "," + z);
        ArrayList<String> newFlags = new ArrayList<String>();
        for (Flag flag : effect.getFlags()) {
            newFlags.add(flag.name());
        }
        plugin.yaml.set(player + ".flags", newFlags);
        plugin.yaml.set(player + ".preset", this.effect.getName());
        plugin.yaml.set(player + ".interval", effect.getInterval());
        plugin.yaml.set(player + ".density", effect.getDensity());
        plugin.yaml.set(player + ".offset", effect.getOffset());

        if (getParticle() != null)
            plugin.yaml.set(player + ".particle", getParticle().name());
        else
            plugin.yaml.set(player + ".particle", Particle.FIREWORKS_SPARK);

        plugin.yaml.set(player + ".size", effect.getSize());
    }

}
