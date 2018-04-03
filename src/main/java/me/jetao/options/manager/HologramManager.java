package me.jetao.options.manager;

import com.google.common.collect.Maps;
import java.util.Map;
import me.jetao.options.hologram.Hologram;
import org.bukkit.entity.Player;

public class HologramManager {

    private Map<Player, Hologram> holograms;

    public HologramManager() {
        this.holograms = Maps.newHashMap();
    }

    public void addHologram(Player player, Hologram hologram) {
        holograms.put(player, hologram);
    }

    public void removeHologram(Player player) {
        holograms.remove(player);
    }

    public Hologram getHologram(Player player) {
        return holograms.get(player);
    }

    public boolean hasHologram(Player player) {
        return holograms.containsKey(player);
    }

    public Map<Player, Hologram> getHolograms() {
        return holograms;
    }
}
