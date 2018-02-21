package me.fzzy.fzzyparticles.listeners;

import me.fzzy.fzzyparticles.FzzyParticles;
import me.fzzy.fzzyparticles.ParticleEffectType;
import me.fzzy.fzzyparticles.ParticleP;
import me.fzzy.fzzyparticles.SavedPreset;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onParticleInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (event.getInventory().getTitle().equals(ChatColor.AQUA + "Particle Selector")) {
                ItemStack i = event.getCurrentItem();
                ItemMeta meta = i.getItemMeta();
                if (meta.getDisplayName().equals(ChatColor.RED + "None")) {
                    FzzyParticles.getPlayerParticle(player).setEffectType(ParticleEffectType.NONE);
                    player.closeInventory();
                } else {
                    for (SavedPreset preset : FzzyParticles.presets) {
                        if (preset.getName().equals(ChatColor.stripColor(meta.getDisplayName()))) {
                            for (ParticleP p : FzzyParticles.players) {
                                if (p.getPlayer().equals(player.getUniqueId())) {
                                    FzzyParticles.players.remove(p);
                                    break;
                                }
                            }
                            FzzyParticles.players.add(new ParticleP(player.getUniqueId(), preset));
                            player.closeInventory();
                            break;
                        }
                    }
                }
                event.setCancelled(true);
            }
        }
    }

}
