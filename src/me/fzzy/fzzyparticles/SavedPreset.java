package me.fzzy.fzzyparticles;

import java.util.ArrayList;
import java.util.List;

import me.fzzy.fzzyparticles.effects.*;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SavedPreset {

    private String name;

    private Vector vel;
    private ArrayList<Flag> flags;
    private int interval;
    private int density;
    private double offset;
    private double size;
    private ItemStack icon;

    private ParticleEffect effect;

    public SavedPreset(String name, ParticleEffect effect, Vector vel, ArrayList<Flag> flags, int interval, int density, double offset, double size, ItemStack icon) {
        this.name = name;
        this.vel = vel;
        this.flags = flags;
        this.interval = interval;
        this.effect = effect;
        this.density = density;
        this.offset = offset;
        this.size = size;
        this.icon = icon;
    }

    public SavedPreset(String name) {
        int density = FzzyParticles.savedPresets.getInteger(name + ".density");
        ParticleEffectType type = ParticleEffectType.valueOf(FzzyParticles.savedPresets.getString(name + ".effect"));
        Particle particle = Particle.valueOf(FzzyParticles.savedPresets.getString(name + ".particle"));
        ParticleEffect effect = null;
        switch (type) {
            case LINE:
                effect = new Line(particle);
                break;
            case RINGS:
                effect = new Rings(particle);
                break;
            case ROSE:
                effect = new Rose(particle);
                break;
            case SPIRAL:
                effect = new Spiral(particle);
                break;
            case WALL:
                effect = new Wall(particle);
                break;
            case WAVE:
                effect = new Wave(particle);
                break;
            default:
                break;
        }
        ArrayList<String> flags = (ArrayList<String>) FzzyParticles.savedPresets.getStringList(name + ".flags");
        ArrayList<Flag> nFlags = new ArrayList<Flag>();
        for (String s : flags) {
            nFlags.add(Flag.valueOf(s));
        }
        int interval = FzzyParticles.savedPresets.getInteger(name + ".interval");
        double offset = FzzyParticles.savedPresets.getDouble(name + ".offset");
        double size = FzzyParticles.savedPresets.getDouble(name + ".size");
        ItemStack icon = (ItemStack) FzzyParticles.savedPresets.get(name + ".icon");

        String[] values = FzzyParticles.savedPresets.getString(name + ".vector").split(",");
        double x = Double.parseDouble(values[0]);
        double y = Double.parseDouble(values[1]);
        double z = Double.parseDouble(values[2]);
        Vector vector = new Vector(x, y, z);

        this.density = density;
        this.effect = effect;
        this.flags = nFlags;
        this.interval = interval;
        this.offset = offset;
        this.size = size;
        this.effect = effect;
        this.icon = icon;
        this.vel = vector;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(ItemStack i) {
        this.icon = i;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public String getName() {
        return name;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public Vector getVector() {
        return vel;
    }

    public void setVector(Vector vel) {
        this.vel = vel;
    }

    public ArrayList<Flag> getFlags() {
        return flags;
    }

    public void setFlags(ArrayList<Flag> list) {
        this.flags = list;
    }

    public void addFlag(Flag flag) {
        this.flags.add(flag);
    }

    public boolean hasFlag(Flag flag) {
        if (flags.contains(flag))
            return true;
        else
            return false;
    }

    public void setInterval(int i) {
        this.interval = i;
    }

    public int getInterval() {
        return this.interval;
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

    public int getDensity() {
        return this.density;
    }

    public void setDensity(int i) {
        this.density = i;
    }

    public double getOffset() {
        return this.offset;
    }

    public void setOffset(double i) {
        this.offset = i;
    }

    public void setSize(double i) {
        this.size = i;
    }

    public double getSize() {
        return this.size;
    }

    public SavedPreset getCopy() {
        return new SavedPreset(name, effect, vel, flags, interval, density, offset, size, icon);
    }

    public void setEffectType(ParticleEffectType type) {
        switch (type) {
            case LINE:
                setEffect(new Line(getParticle()));
                break;
            case RINGS:
                setEffect(new Rings(getParticle()));
                break;
            case ROSE:
                setEffect(new Rose(getParticle()));
                break;
            case SPIRAL:
                setEffect(new Spiral(getParticle()));
                break;
            case WALL:
                setEffect(new Wall(getParticle()));
                break;
            case WAVE:
                setEffect(new Wave(getParticle()));
                break;
            case NONE:
                setEffect(null);
            default:
                setEffect(null);
                break;
        }
    }

    public ParticleEffectType getEffectType() {
        return this.effect.getType();
    }

    public void save() {
        List<String> presets = FzzyParticles.savedPresets.getStringList("presets");
        if (!presets.contains(name))
            presets.add(name);
        FzzyParticles.savedPresets.set("presets", presets);
        FzzyParticles.savedPresets.set(name + ".density", density);
        FzzyParticles.savedPresets.set(name + ".effect", effect.getType().name());
        for (Flag flag : flags) {
            FzzyParticles.savedPresets.set(name + ".flags", flag.name());
        }
        FzzyParticles.savedPresets.set(name + ".interval", interval);
        FzzyParticles.savedPresets.set(name + ".offset", offset);
        FzzyParticles.savedPresets.set(name + ".particle", effect.getParticle().name());
        FzzyParticles.savedPresets.set(name + ".size", size);
        FzzyParticles.savedPresets.set(name + ".icon", icon);
        double x = vel.getX();
        double y = vel.getY();
        double z = vel.getZ();
        FzzyParticles.savedPresets.set(name + ".vector", x + "," + y + "," + z);
        FzzyParticles.savedPresets.save();
    }

    public void setNoSave() {
        List<String> presets = FzzyParticles.savedPresets.getStringList("presets");
        if (!presets.contains(name))
            presets.add(name);
        FzzyParticles.savedPresets.set("presets", presets);
        FzzyParticles.savedPresets.set(name + ".density", density);
        FzzyParticles.savedPresets.set(name + ".effect", effect.getType().name());
        List<String> list = new ArrayList<String>();
        for (Flag flag : flags) {
            list.add(flag.name());
        }
        FzzyParticles.savedPresets.set(name + ".flags", list);
        FzzyParticles.savedPresets.set(name + ".interval", interval);
        FzzyParticles.savedPresets.set(name + ".offset", offset);
        FzzyParticles.savedPresets.set(name + ".particle", effect.getParticle().name());
        FzzyParticles.savedPresets.set(name + ".size", size);
        FzzyParticles.savedPresets.set(name + ".icon", icon);
        double x = vel.getX();
        double y = vel.getY();
        double z = vel.getZ();
        FzzyParticles.savedPresets.set(name + ".vector", x + "," + y + "," + z);
    }

}
