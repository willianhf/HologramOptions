package me.jetao.options.hologram;

import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import me.jetao.options.reflection.Reflection;
import me.jetao.options.reflection.Reflection.PackageType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class Hologram {

    private static final double DISTANCE = 0.23D;

    private static Class<?> craftWorld, nmsEntity, nmsWorld, nmsArmorStand, nmsEntityLiving, spawnPacket, destroyPacket;

    static {
        try {
            craftWorld = PackageType.CRAFTBUKKIT.getClass("CraftWorld");
            nmsEntity = PackageType.MINECRAFT_SERVER.getClass("Entity");
            nmsWorld = PackageType.MINECRAFT_SERVER.getClass("World");
            nmsArmorStand = PackageType.MINECRAFT_SERVER.getClass("EntityArmorStand");
            nmsEntityLiving = PackageType.MINECRAFT_SERVER.getClass("EntityLiving");
            spawnPacket = PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutSpawnEntityLiving");
            destroyPacket = PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutEntityDestroy");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Location location;
    private String title;
    private List<Option> options;
    private List<Integer> ids;
    private List<Object> entities;
    private List<Player> seeing;

    private Option currentOption;

    public Hologram(String title, Location location) {
        this.title = title;
        this.location = location;

        this.options = Lists.newArrayList();
        this.ids = Lists.newArrayList();
        this.entities = Lists.newArrayList();
        this.seeing = Lists.newArrayList();
    }

    public Hologram addLine(String text, Response response) {
        options.add(new Option(text, response));
        currentOption = options.get(0);
        return this;
    }

    public void scroll(int direction) {
        int nextId = options.indexOf(currentOption) + direction;
        if (nextId > options.size() - 1) {
            nextId = 0;
        }
        if (nextId < 0) {
            nextId = options.size() - 1;
        }
        options.get(options.indexOf(currentOption)).setText(currentOption.getText().replace("§a§l>", "").replace("§a§l<", "").trim());
        currentOption = options.get(nextId);
        options.get(nextId).setText("§a§l> §r" + currentOption.getText() + " §a§l<");
        update();
    }

    public void showTo(Player... players) {
        Location current = location.clone().add(0, (DISTANCE * options.size()) - 1.97D, 0);

        Object[] titlePacket = getCreatePacket(current, ChatColor.translateAlternateColorCodes('&', title));
        ids.add((Integer) titlePacket[1]);

        Arrays.asList(players).forEach(player -> Reflection.sendPacket(player, titlePacket[0]));

        current.subtract(0, DISTANCE + .07, 0);

        options.forEach(line -> {
            Object[] packet = getCreatePacket(current, ChatColor.translateAlternateColorCodes('&', line.getText()));
            ids.add((Integer) packet[1]);

            Arrays.asList(players).forEach(player -> Reflection.sendPacket(player, packet[0]));

            current.subtract(0, DISTANCE, 0);
        });
        List<Player> playerList = Arrays.asList(players);
        seeing.addAll(playerList);
    }

    public void hideTo(Player... players) {
        ids.forEach(id -> Arrays.asList(players).forEach(player -> Reflection.sendPacket(player, getRemovePacket(id))));
        seeing.removeAll(Arrays.asList(players));
    }

    private void spawnHologram(String text, Location location) {
        try {
            Object craftWorld = Hologram.craftWorld.cast(location.getWorld());
            Object entityObject = nmsArmorStand.getConstructor(nmsWorld)
                .newInstance(Hologram.craftWorld.getMethod("getHandle").invoke(craftWorld));

            configureHologram(entityObject, text, location);

            Hologram.craftWorld.getMethod("addEntity", nmsEntity, CreatureSpawnEvent.SpawnReason.class)
                .invoke(craftWorld, entityObject, CreatureSpawnEvent.SpawnReason.CUSTOM);

            entities.add(entityObject);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void remove() {
        if (!entities.isEmpty()) {
            entities.forEach(entity -> removeEntity(entity));
        } else {
            hideTo(seeing.toArray(new Player[seeing.size()]));
        }
    }

    private void removeEntity(Object entity) {
        try {
            Object craftWorld = Hologram.craftWorld.cast(location.getWorld());

            nmsWorld.getMethod("removeEntity", nmsEntity).invoke(Hologram.craftWorld.getMethod("getHandle").invoke(craftWorld), entity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Object[] getCreatePacket(Location location, String text) {
        try {
            Object entityObject = nmsArmorStand.getConstructor(nmsWorld)
                .newInstance(craftWorld.getMethod("getHandle").invoke(craftWorld.cast(location.getWorld())));
            Object id = entityObject.getClass().getMethod("getId").invoke(entityObject);

            configureHologram(entityObject, text, location);

            return new Object[]{spawnPacket.getConstructor(nmsEntityLiving).newInstance(entityObject), id};
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Object getRemovePacket(int id) {
        try {
            return destroyPacket.getConstructor(int[].class).newInstance(new int[]{id});
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void update() {
        if (!entities.isEmpty()) {
            for (int i = 0; i < entities.size(); i++) {
                Object ent = entities.get(i);

                if (i > options.size() - 1) {
                    removeEntity(ent);
                }
            }
            Location current = location.clone().add(0, (DISTANCE * options.size()) - 1.97D, 0);
            for (int i = 0; i < options.size(); i++) {
                String text = ChatColor.translateAlternateColorCodes('&', options.get(i).getText());
                if (i >= entities.size()) {
                    spawnHologram(text, current);
                } else {
                    configureHologram(entities.get(i), text, current);
                }
                current.subtract(0, DISTANCE, 0);
            }
        } else {
            Player[] players = seeing.toArray(new Player[seeing.size()]);
            hideTo(players);
            showTo(players);
        }
    }

    private void configureHologram(Object entityObject, String text, Location location) {
        try {
            Method setCustomName = entityObject.getClass().getMethod("setCustomName", String.class);
            Method setCustomNameVisible = entityObject.getClass().getMethod("setCustomNameVisible", boolean.class);
            Method setGravity = entityObject.getClass().getMethod("setGravity", boolean.class);
            Method setLocation = entityObject.getClass()
                .getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            Method setInvisible = entityObject.getClass().getMethod("setInvisible", boolean.class);

            setCustomName.invoke(entityObject, text);
            setCustomNameVisible.invoke(entityObject, true);
            setGravity.invoke(entityObject, true);
            setLocation.invoke(entityObject, location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
            setInvisible.invoke(entityObject, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Option getCurrentOption() {
        return currentOption;
    }

    public List<Option> getOptions() {
        return options;
    }

    public Location getLocation() {
        return location;
    }

}
