/*
 *  Copyright (C) 2022  cakemc-ru
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.cakemc.framedimage;

import com.jnngl.mapcolor.ColorMatcher;
import com.jnngl.mapcolor.matchers.BufferedImageMatcher;
import com.jnngl.mapcolor.matchers.CachedColorMatcher;
import com.jnngl.mapcolor.palette.Palette;
import io.netty.channel.Channel;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.cakemc.framedimage.command.FiCommand;
import ru.cakemc.framedimage.config.Config;
import ru.cakemc.framedimage.config.Frames;
import ru.cakemc.framedimage.config.Messages;
import ru.cakemc.framedimage.injection.Injector;
import ru.cakemc.framedimage.listener.PlayerListener;
import ru.cakemc.framedimage.listener.HandshakeListener;
import ru.cakemc.framedimage.protocol.Packet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FramedImage extends JavaPlugin {

  private final Injector injector = new Injector();
  private final File configFile = new File(getDataFolder(), "config.yml");
  private final File messagesFile = new File(getDataFolder(), "messages.yml");
  private final File framesFile = new File(getDataFolder(), "frames.yml");
  private final Map<String, Channel> playerChannels = new ConcurrentHashMap<>();
  private final Map<String, List<FrameDisplay>> displays = new ConcurrentHashMap<>();
  private final Map<Palette, ColorMatcher> colorMatchers = new ConcurrentHashMap<>();
  private final Map<FrameDisplay, BukkitTask> updatableDisplays = new ConcurrentHashMap<>();
  private final Set<String> loggingPlayers = ConcurrentHashMap.newKeySet();

  @Override
  public void onEnable() {
    injector.addInjector(channel -> {
      channel.pipeline().addAfter("splitter", "framedimage:handshake", new HandshakeListener(this));
    });

    injector.inject();
    getLogger().info("Successfully injected!");

    reload();

    getCommand("fi").setExecutor(new FiCommand(this));
    getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

    Metrics metrics = new Metrics(this, 16966);
    metrics.addCustomChart(new SimplePie("dithering", () -> String.valueOf(Config.IMP.DITHERING)));
    metrics.addCustomChart(new SimplePie("glow", () -> String.valueOf(Config.IMP.GLOW)));
  }

  @Override
  public void onDisable() {
    removeAll();
    playerChannels.clear();
  }

  public Channel getPlayerChannel(String name) {
    return playerChannels.get(name);
  }

  public Channel getPlayerChannel(Player player) {
    return getPlayerChannel(player.getName());
  }

  public Map<String, Channel> getPlayerChannels() {
    return playerChannels;
  }

  public Map<String, List<FrameDisplay>> getDisplays() {
    return displays;
  }

  public Set<String> getLoggingPlayers() {
    return loggingPlayers;
  }

  public void displayNextFrame(FrameDisplay display) {
    Location location = display.getLocation();
    Collection<Player> players = location.getNearbyPlayers(256);
    List<Packet> packets = display.getNextFramePackets();
    players.forEach(player -> {
      if (!loggingPlayers.contains(player.getName())) {
        Channel channel = getPlayerChannel(player);
        if (channel != null) {
          packets.forEach(channel::write);
          channel.flush();
        }
      }
    });
  }

  public void spawn(FrameDisplay display, Player player) {
    Channel channel = getPlayerChannel(player);
    if (channel != null) {
      display.getSpawnPackets().forEach(channel::write);
      display.getNextFramePackets().forEach(channel::write);
      channel.flush();
    }
  }

  public void spawn(FrameDisplay display) {
    World world = display.getLocation().getWorld();
    List<Player> players = world.getPlayers();
    players.forEach(player -> spawn(display, player));

    if (display.getNumFrames() > 1) {
      updatableDisplays.put(
          display,
          Bukkit.getScheduler().runTaskTimer(
              this,
              () -> displayNextFrame(display),
              1L, 1L
          )
      );
    }
  }

  public void spawn(Player player) {
    World world = player.getLocation().getWorld();
    List<FrameDisplay> displays = this.displays.get(world.getName());
    if (displays != null) {
      displays.forEach(display -> spawn(display, player));
    }
  }

  public void destroy(FrameDisplay display, Player player) {
    Channel channel = getPlayerChannel(player);
    if (channel != null) {
      display.getDestroyPackets().forEach(channel::write);
      channel.flush();
    }
  }

  public void destroy(FrameDisplay display) {
    BukkitTask updater = updatableDisplays.remove(display);
    if (updater != null) {
      updater.cancel();
    }

    World world = display.getLocation().getWorld();
    List<Player> players = world.getPlayers();
    players.forEach(player -> destroy(display, player));
  }

  public void destroyAll() {
    displays.values()
        .stream()
        .flatMap(List::stream)
        .forEach(this::destroy);
  }

  public void add(FrameDisplay display) {
    spawn(display);
    World world = display.getLocation().getWorld();
    displays.computeIfAbsent(world.getName(), k -> new ArrayList<>()).add(display);

    try {
      synchronized (Frames.IMP.MUTEX) {
        Frames.IMP.FRAMES.put(display.getUUID().toString(), new Frames.FrameNode(display, getDataFolder()));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void remove(FrameDisplay display) {
    destroy(display);
    World world = display.getLocation().getWorld();
    displays.get(world.getName()).remove(display);

    synchronized (Frames.IMP.MUTEX) {
      Frames.IMP.FRAMES.remove(display.getUUID().toString());
    }
  }

  public void removeAll() {
    destroyAll();
    displays.clear();

    synchronized (Frames.IMP.MUTEX) {
      Frames.IMP.FRAMES.clear();
    }
  }

  public void saveFrames() {
    Frames.IMP.save(framesFile);
  }

  public void reload() {
    removeAll();

    Config.IMP.reload(configFile);
    Messages.IMP.reload(messagesFile);
    Frames.IMP.reload(framesFile);

    for (Palette palette : Palette.ALL_PALETTES) {
      colorMatchers.put(
          palette,
          Config.IMP.DITHERING
              ? new BufferedImageMatcher(palette)
              : new CachedColorMatcher(palette)
      );
    }

    synchronized (Frames.IMP.MUTEX) {
      getLogger().info("Loading " + Frames.IMP.FRAMES.size() + " images.");

      Frames.IMP.FRAMES.forEach(
          (uuid, node) -> {
            try {
              add(node.createFrameDisplay(this, UUID.fromString(uuid)));
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
      );
    }

    saveFrames();
  }

  public Map<Palette, ColorMatcher> getColorMatchers() {
    return colorMatchers;
  }
}
