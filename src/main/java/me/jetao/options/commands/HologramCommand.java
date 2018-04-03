package me.jetao.options.commands;

import java.lang.reflect.Field;
import me.jetao.options.OptionsPlugin;
import me.jetao.options.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HologramCommand extends Command {

    public HologramCommand() {
        super("hologram");
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap map = (CommandMap) field.get(Bukkit.getServer());
            map.register(this.getName(), this);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        Hologram hologram = new Hologram("&aTítulo teste",
            player.getLocation().add(player.getLocation().getDirection().normalize().setY(0).multiply(1.3)).add(0, .7, 0));
        hologram.addLine("Teste 1", (interact, otherHologram) -> interact.sendMessage("1"))
            .addLine("Teste 2", (interact, otherHologram) -> interact.sendMessage("2"))
            .addLine("Teste 3", (interact, otherHologram) -> interact.sendMessage("3")).addLine("&cFechar", (interact, otherHologram) -> {
            interact.sendMessage("&cVocê fechou o menu de opções.");
            otherHologram.remove();
            OptionsPlugin.getHologramManager().removeHologram(player);
        });
        hologram.showTo(player);
        OptionsPlugin.getHologramManager().addHologram(player, hologram);
        return false;
    }

}
