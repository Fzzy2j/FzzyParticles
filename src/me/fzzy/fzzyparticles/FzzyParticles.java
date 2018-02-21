package me.fzzy.fzzyparticles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.fzzy.fzzyparticles.effects.*;
import me.fzzy.fzzyparticles.listeners.InventoryClickListener;
import me.fzzy.fzzyparticles.listeners.PlayerMoveListener;
import me.fzzy.fzzyparticles.listeners.PlayerQuitListener;
import me.fzzy.fzzyparticles.util.Yaml;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class FzzyParticles extends JavaPlugin {

    public static FzzyParticles instance;
    public static ArrayList<ParticleP> players;
    public static ArrayList<SavedPreset> presets;
    public static final ParticleEffectType DEFAULT_PARTICLE = ParticleEffectType.NONE;
    public static Yaml yaml;
    public static Yaml savedPresets;

    public void onEnable() {
        instance = this;
        (new File(this.getDataFolder().getAbsolutePath())).mkdirs();
        this.yaml = new Yaml(getDataFolder().getAbsolutePath() + File.separator + "players.yml");
        this.savedPresets = new Yaml(getDataFolder().getAbsolutePath() + File.separator + "savedPresets.yml");
        players = new ArrayList<>();
        presets = new ArrayList<>();

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerQuitListener(), this);
        pm.registerEvents(new PlayerMoveListener(), this);
        pm.registerEvents(new InventoryClickListener(), this);

        if (!savedPresets.contains("presets")) {
            SavedPreset spiral = new SavedPreset("Spiral", new Spiral(Particle.FIREWORKS_SPARK), new Vector(0, 0, 0), asList(Flag.MOVING), 0, 3, 0.4D, 1, new ItemStack(Material.WEB));
            SavedPreset stripe = new SavedPreset("Stripe", new Wall(Particle.DRIP_WATER), new Vector(0, 0, 0), asList(Flag.MOVING), 0, 0, 0.4D, 6, new ItemStack(Material.ANVIL));
            SavedPreset line = new SavedPreset("Line", new Line(Particle.HEART), new Vector(0, 0, 0), asList(Flag.MOVING), 0, 0, 0.3, 2, new ItemStack(Material.STICK));
            SavedPreset wave = new SavedPreset("Wave", new Wave(Particle.DRIP_LAVA), new Vector(0, 0, 0), asList(Flag.MOVING), 0, 1, 0.4D, 1, new ItemStack(Material.WATER_BUCKET));
            SavedPreset rose = new SavedPreset("Rose", new Rose(Particle.FLAME), new Vector(0, 0, 0), asList(Flag.MOVING), 0, 4, 1, 0.4D, new ItemStack(Material.RED_ROSE));
            ArrayList<Flag> flags = new ArrayList<>();
            flags.add(Flag.MOVING);
            flags.add(Flag.ON_GROUND);
            SavedPreset rings = new SavedPreset("Rings", new Rings(Particle.FIREWORKS_SPARK), new Vector(0.5, 0, 0), flags, 0, 10, 0, 0, new ItemStack(Material.BOWL));
            spiral.setNoSave();
            stripe.setNoSave();
            line.setNoSave();
            rings.setNoSave();
            wave.setNoSave();
            rose.setNoSave();
            savedPresets.save();
        }
        List<String> list = savedPresets.getStringList("presets");
        for (String s : list) {
            presets.add(new SavedPreset(s));
        }

        new Timer().runTaskTimer(this, 20L, 1L);
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayerParticle(player);
        }
    }

    public void onDisable() {
        for (ParticleP p : players) {
            p.unloadNoSave(this);
        }
        for (SavedPreset preset : presets) {
            preset.setNoSave();
        }
        savedPresets.save();
        yaml.save();
    }

    public ArrayList<Flag> asList(Flag flag) {
        ArrayList<Flag> list = new ArrayList<Flag>();
        list.add(flag);
        return list;
    }

    public static ParticleP getPlayerParticle(Player player) {
        ParticleP particle = null;
        for (ParticleP p : players) {
            if (p.getPlayer().equals(player.getUniqueId()))
                particle = p;
        }
        if (particle == null) {
            ParticleP part = new ParticleP(player.getUniqueId());
            part.load(player);
            particle = part;
        }
        return particle;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
        if (label.equalsIgnoreCase("fzzyp") || label.equalsIgnoreCase("fp")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (presets.size() > 0) {
                        int presetSize = 0;
                        Inventory inv = Bukkit.createInventory(null, (int) (Math.ceil((double) (presets.size() + 1) / (double) 9) * 9), ChatColor.AQUA + "Particle Selector");
                        ItemStack i = new ItemStack(Material.ROTTEN_FLESH);
                        ItemMeta meta = i.getItemMeta();
                        meta.setDisplayName(ChatColor.RED + "None");
                        i.setItemMeta(meta);
                        inv.addItem(i);
                        for (SavedPreset preset : presets) {
                            String permission = "fzzyparticles.preset." + preset.getName();
                            if (player.hasPermission(permission.toLowerCase())) {
                                presetSize++;
                                ItemStack icon = preset.getIcon();
                                ItemMeta iconMeta = icon.getItemMeta();
                                iconMeta.setDisplayName(ChatColor.AQUA + preset.getName());
                                icon.setItemMeta(iconMeta);
                                inv.addItem(icon);


                                int x = 30;

                                switch(x) {
                                    case 10:
                                        System.out.println("Value of X is 10");
                                        break;
                                    case 20:
                                        System.out.println("Value of X is 20");
                                        break;
                                    case 30:
                                        System.out.println("Value of X is 30");
                                        break;
                                    default:
                                        System.out.println("This is else statement");
                                        break;
                                }





                            }
                        }
                        if (presetSize > 0) {
                            player.openInventory(inv);
                            return false;
                        } else {
                            player.sendMessage(ChatColor.RED + "You have no preset permissions, displaying help instead");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "This command is player only!");
                    return false;
                }
            }
            if (sender.hasPermission("fzzyparticles.list")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("effects")) {
                        sender.sendMessage(ChatColor.GOLD + "= Effects =");
                        for (ParticleEffectType effect : ParticleEffectType.values()) {
                            sender.sendMessage(ChatColor.AQUA + effect.name());
                        }
                        return false;
                    }
                    if (args[0].equalsIgnoreCase("particles")) {
                        sender.sendMessage(ChatColor.GOLD + "= Particles =");
                        for (Particle effect : Particle.values()) {
                            sender.sendMessage(ChatColor.AQUA + effect.name());
                        }
                        return false;
                    }
                    if (args[0].equalsIgnoreCase("flags")) {
                        sender.sendMessage(ChatColor.GOLD + "= Flags =");
                        for (Flag effect : Flag.values()) {
                            sender.sendMessage(ChatColor.AQUA + effect.name());
                        }
                        return false;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return false;
            }
            if (sender.hasPermission("fzzyparticles.help")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        displayHelpHelp(sender);
                        return false;
                    }
                }
            }
            if (args.length == 2) {
                if (sender.hasPermission("fzzyparticles.help")) {
                    if (args[0].equalsIgnoreCase("help")) {
                        if (args[1].equalsIgnoreCase("list")) {
                            displayListHelp(sender);
                            return false;
                        }
                        if (args[1].equalsIgnoreCase("preset")) {
                            displayPresetHelp(sender);
                            return false;
                        }
                        if (args[1].equalsIgnoreCase("effect")) {
                            displayEffectHelp(sender);
                            return false;
                        }
                    }
                }
                if (sender.hasPermission("fzzyparticles.info")) {
                    if (args[0].equalsIgnoreCase("info")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        ParticleP p = this.getPlayerParticle(target);
                        sender.sendMessage(ChatColor.GREEN + "Info for " + target.getName());
                        sender.sendMessage(ChatColor.AQUA + "Preset Name: " + p.getPreset().getName());
                        sender.sendMessage(ChatColor.AQUA + "Particle: " + p.getPreset().getParticle());
                        sender.sendMessage(ChatColor.AQUA + "Density: " + p.getPreset().getDensity());
                        sender.sendMessage(ChatColor.AQUA + "Interval: " + p.getPreset().getInterval());
                        sender.sendMessage(ChatColor.AQUA + "Offset: " + p.getPreset().getOffset());
                        Vector vel = p.getPreset().getVector();
                        sender.sendMessage(ChatColor.AQUA + "Vector: " + vel.getX() + ", " + vel.getY() + ", " + vel.getZ());
                        String display = "";
                        for (Flag flag : p.getPreset().getFlags()) {
                            display += ", " + flag.name();
                        }
                        if (p.getPreset().getFlags().size() > 0)
                            sender.sendMessage(ChatColor.AQUA + "Flags: " + display.substring(2));
                        else
                            sender.sendMessage(ChatColor.AQUA + "Flags: NONE");
                        if (p.getPreset().getEffect() != null)
                            sender.sendMessage(ChatColor.AQUA + "Effect: " + p.getPreset().getEffectType().name());
                        else
                            sender.sendMessage(ChatColor.AQUA + "Effect: NONE");
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
                if (sender.hasPermission("fzzyparticles.save")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (args[0].equalsIgnoreCase("save")) {
                            if (player.getItemInHand() == null)
                                player.sendMessage(ChatColor.RED + "You must hold something in your hand to use as an icon!");
                            else {
                                ParticleP p = this.getPlayerParticle(player);
                                SavedPreset preset = new SavedPreset(args[1], p.getPreset().getEffect(), p.getPreset().getVector(), p.getPreset().getFlags(), p.getPreset().getInterval(), p.getPreset().getDensity(),
                                        p.getPreset().getOffset(), p.getPreset().getSize(), player.getItemInHand());
                                preset.save();
                                for (SavedPreset pres : presets) {
                                    if (pres.getName().equals(preset.getName())) {
                                        presets.remove(pres);
                                    }
                                }
                                this.presets.add(preset);
                                player.sendMessage(ChatColor.AQUA + "Preset saved with name '" + args[1] + "' and icon '" + player.getItemInHand().getType().name() + "'");
                            }
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "This command is player only!");
                        return false;
                    }
                }
                if (sender.hasPermission("fzzyparticles.delete")) {
                    if (args[0].equalsIgnoreCase("delete")) {
                        for (SavedPreset preset : presets) {
                            if (preset.getName().equalsIgnoreCase(args[1])) {
                                List<String> list = this.savedPresets.getStringList("presets");
                                list.remove(preset.getName());
                                this.savedPresets.set("presets", list);
                                this.savedPresets.save();
                                presets.remove(preset);
                                sender.sendMessage(ChatColor.AQUA + "Preset '" + preset.getName() + "' deleted");
                                return false;
                            }
                        }
                        return false;
                    }
                }
            }
            if (args.length == 3) {
                if (sender.hasPermission("fzzyparticles.set")) {
                    if (args[0].equalsIgnoreCase("set")) {
                        try {
                            Player target = Bukkit.getPlayer(args[1]);
                            ParticleP targetP = this.getPlayerParticle(target);
                            targetP.makeCustom();
                            ParticleEffectType type = ParticleEffectType.valueOf(args[2].toUpperCase());
                            targetP.setEffectType(type);
                            sender.sendMessage(ChatColor.AQUA + "Particle effect for " + target.getName() + " changed to " + args[2].toUpperCase());
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + "Can't find particleEffect '" + args[2].toUpperCase() + "' or can't find player '" + args[1] + "'");
                            sender.sendMessage(ChatColor.RED + "You can find a list of particle effects by typing " + ChatColor.GREEN + "/particle list");
                            Bukkit.getLogger().log(Level.SEVERE, "Can't find particleEffect or can't find player", e);
                        }
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
                if (sender.hasPermission("fzzyparticles.interval")) {
                    if (args[0].equalsIgnoreCase("interval")) {
                        try {
                            Player target = Bukkit.getPlayer(args[1]);
                            ParticleP targetP = this.getPlayerParticle(target);
                            targetP.makeCustom();
                            targetP.getPreset().setInterval(Integer.parseInt(args[2]));
                            sender.sendMessage(ChatColor.AQUA + "Interval for " + target.getName() + " changed to " + args[2]);
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + args[2] + " is not a number or can't find player '" + args[1] + "'");
                            Bukkit.getLogger().log(Level.SEVERE, "Integer parse error or can't find player", e);
                        }
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
                if (sender.hasPermission("fzzyparticles.offset")) {
                    if (args[0].equalsIgnoreCase("offset")) {
                        try {
                            Player target = Bukkit.getPlayer(args[1]);
                            ParticleP targetP = this.getPlayerParticle(target);
                            targetP.makeCustom();
                            targetP.getPreset().setOffset(Double.parseDouble(args[2]));
                            sender.sendMessage(ChatColor.AQUA + "Offset for " + target.getName() + " changed to " + args[2]);
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + args[2] + " is not a number or can't find player '" + args[1] + "'");
                            Bukkit.getLogger().log(Level.SEVERE, "Integer parse error or can't find player", e);
                        }
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
                if (sender.hasPermission("fzzyparticles.size")) {
                    if (args[0].equalsIgnoreCase("size")) {
                        try {
                            Player target = Bukkit.getPlayer(args[1]);
                            ParticleP targetP = this.getPlayerParticle(target);
                            targetP.makeCustom();
                            targetP.getPreset().setSize(Double.parseDouble(args[2]));
                            sender.sendMessage(ChatColor.AQUA + "Size for " + target.getName() + " changed to " + args[2]);
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + args[2] + " is not a number or can't find player '" + args[1] + "'");
                            Bukkit.getLogger().log(Level.SEVERE, "Integer parse error or can't find player", e);
                        }
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
                if (sender.hasPermission("fzzyparticles.particle")) {
                    if (args[0].equalsIgnoreCase("particle")) {
                        try {
                            Player target = Bukkit.getPlayer(args[1]);
                            ParticleP targetP = this.getPlayerParticle(target);
                            targetP.makeCustom();
                            targetP.setParticle(Particle.valueOf(args[2].toUpperCase()));
                            sender.sendMessage(ChatColor.AQUA + "Particle for " + target.getName() + " changed to " + args[2].toUpperCase());
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + "Can't find particle '" + args[2] + "' or can't find player '" + args[1] + "'");
                            sender.sendMessage(ChatColor.RED + "You can find a list of particles by typing " + ChatColor.GREEN + "/particle particles");
                            Bukkit.getLogger().log(Level.SEVERE, "Can't find particle or can't find player", e);
                        }
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
                if (sender.hasPermission("fzzyparticles.density")) {
                    if (args[0].equalsIgnoreCase("density")) {
                        try {
                            Player target = Bukkit.getPlayer(args[1]);
                            ParticleP targetP = this.getPlayerParticle(target);
                            targetP.makeCustom();
                            targetP.getPreset().setDensity(Integer.parseInt(args[2]));
                            sender.sendMessage(ChatColor.AQUA + "Density for " + target.getName() + " changed to " + args[2]);
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + args[2] + " is not a number or can't find player '" + args[1] + "'");
                            Bukkit.getLogger().log(Level.SEVERE, "Integer parse error or can't find player", e);
                        }
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
            }
            if (args.length == 5) {
                if (sender.hasPermission("fzzyparticles.velocity")) {
                    if (args[0].equalsIgnoreCase("velocity")) {
                        try {
                            Player target = Bukkit.getPlayer(args[1]);
                            ParticleP targetP = this.getPlayerParticle(target);
                            targetP.makeCustom();
                            double x = Double.parseDouble(args[2]);
                            double y = Double.parseDouble(args[3]);
                            double z = Double.parseDouble(args[4]);
                            targetP.getPreset().setVector(new Vector(x, y, z));
                            sender.sendMessage(ChatColor.AQUA + "Particle velocity for " + target.getName() + " changed to " + args[2] + ", " + args[3] + ", " + args[4]);
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.AQUA + "Input must be a number!");
                            Bukkit.getLogger().log(Level.SEVERE, "Integer parse error", e);
                        }
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
            }
            if (args.length > 2) {
                if (sender.hasPermission("fzzyparticles.flag")) {
                    if (args[0].equalsIgnoreCase("flag")) {
                        try {
                            Player target = Bukkit.getPlayer(args[1]);
                            ParticleP particle = this.getPlayerParticle(target);
                            particle.makeCustom();
                            ArrayList<Flag> flags = new ArrayList<Flag>();
                            String output = "";
                            for (int i = 0; i < args.length; i++) {
                                if (i > 1) {
                                    Flag flag = Flag.valueOf(args[i].toUpperCase());
                                    flags.add(flag);
                                    output += ", " + args[i].toLowerCase();
                                }
                            }
                            particle.getPreset().setFlags(flags);
                            sender.sendMessage(ChatColor.AQUA + "Flags for " + target.getName() + " changed to " + output.substring(2));
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + "Can't find flag(s) or can't find player '" + args[1] + "'");
                            sender.sendMessage(ChatColor.RED + "You can find a list of flags by typing " + ChatColor.GREEN + "/particle flags");
                            Bukkit.getLogger().log(Level.SEVERE, "Can't find flag or can't find player", e);
                        }
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
            }
            displayHelpHelp(sender);
        }
        return true;
    }

    public void displayHelpHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "= Help Commands =");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
        sender.sendMessage(ChatColor.DARK_AQUA + "/fzzyp help effect");
        sender.sendMessage(ChatColor.DARK_AQUA + "/fzzyp help preset");
        sender.sendMessage(ChatColor.DARK_AQUA + "/fzzyp help list");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
    }

    public void displayEffectHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "= Effect Commands =");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
        sender.sendMessage(ChatColor.DARK_AQUA + "These commands change the effect currently on a player");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
        sender.sendMessage(ChatColor.AQUA + "/fzzyp set [player] [effect]");
        sender.sendMessage(ChatColor.DARK_AQUA + "Sets a players effect - /fizzyp effects");
        //
        sender.sendMessage(ChatColor.GREEN + "/fzzyp particle [player] [particle]");
        sender.sendMessage(ChatColor.DARK_GREEN + "Sets a players particle - /fizzp particles");
        //
        sender.sendMessage(ChatColor.AQUA + "/fzzyp flag [player] [flag] [flag]...");
        sender.sendMessage(ChatColor.DARK_AQUA + "Sets a players flag - /fizzyp flags");
        //
        sender.sendMessage(ChatColor.GREEN + "/fzzyp velocity [player] [x] [y] [z]");
        sender.sendMessage(ChatColor.DARK_GREEN + "Sets the velocity of the particle on a player, not all particles support this");
        //
        sender.sendMessage(ChatColor.AQUA + "/fzzyp interval [player] [time]");
        sender.sendMessage(ChatColor.DARK_AQUA + "Sets the interval of a players effect");
        //
        sender.sendMessage(ChatColor.GREEN + "/fzzyp size [player] [size]");
        sender.sendMessage(ChatColor.DARK_GREEN + "Sets the size of a players particle");
        //
        sender.sendMessage(ChatColor.AQUA + "/fzzyp offset [player] [amount]");
        sender.sendMessage(ChatColor.DARK_AQUA + "Sets the distance that an effect trails behind a player");
        //
        sender.sendMessage(ChatColor.GREEN + "/fzzyp density [player] [density]");
        sender.sendMessage(ChatColor.DARK_GREEN + "Sets the density of a players effect, this is different per effect");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
    }

    public void displayPresetHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "= Preset Commands =");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
        sender.sendMessage(ChatColor.DARK_AQUA + "These commands are used for presets");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
        sender.sendMessage(ChatColor.AQUA + "/fzzyp save [name]");
        sender.sendMessage(ChatColor.DARK_AQUA + "Saves the effect you currently have on as a preset");
        sender.sendMessage(ChatColor.GREEN + "/fzzyp delete [name]");
        sender.sendMessage(ChatColor.DARK_GREEN + "Deletes a preset");
        sender.sendMessage(ChatColor.AQUA + "/fzzyp info [player name]");
        sender.sendMessage(ChatColor.DARK_AQUA + "Shows you effect information for a player");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
    }

    public void displayListHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "= List Commands =");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
        sender.sendMessage(ChatColor.DARK_AQUA + "These commands are used for listing your available options");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
        sender.sendMessage(ChatColor.AQUA + "/fzzyp effects");
        sender.sendMessage(ChatColor.DARK_AQUA + "Lists available effects");
        sender.sendMessage(ChatColor.GREEN + "/fzzyp particles");
        sender.sendMessage(ChatColor.DARK_GREEN + "Lists available particles");
        sender.sendMessage(ChatColor.AQUA + "/fzzyp flags");
        sender.sendMessage(ChatColor.DARK_AQUA + "Lists available flags");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "------------------------");
    }
}
