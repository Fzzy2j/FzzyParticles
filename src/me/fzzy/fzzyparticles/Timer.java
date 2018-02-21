package me.fzzy.fzzyparticles;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

public class Timer extends BukkitRunnable {

    public static HashMap<UUID, Long> intervals;

    public Timer() {
        Timer.intervals = new HashMap<>();
    }

    @Override
    public void run() {
        for (int i = 0; i < FzzyParticles.players.size(); i++) {
            ParticleP p = FzzyParticles.players.get(i);
            if (p.getPreset().getEffect() != null) {
                if (p.getPreset().getEffect().getType() != ParticleEffectType.NONE) {
                    if (p.getPreset().getInterval() > 0) {
                        if (intervals.containsKey(p.getPlayer())) {
                            if (System.currentTimeMillis() - intervals.get(p.getPlayer()) > p.getPreset().getInterval()) {
                                p.clone().run();
                                intervals.put(p.getPlayer(), System.currentTimeMillis());
                            }
                        } else {
                            p.run();
                            intervals.put(p.getPlayer(), System.currentTimeMillis());
                        }
                    } else {
                        p.run();
                    }
                }
            }
        }
    }

}
