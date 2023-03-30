/*
 *  Copyright (C) 2022  JNNGL
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

package com.jnngl.framedimage.listener;

import com.jnngl.framedimage.FramedImage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

  private final FramedImage plugin;

  public PlayerListener(FramedImage plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    plugin.getScheduler().runDelayed(plugin, () -> {
      Player player = event.getPlayer();
      plugin.getLoggingPlayers().remove(player.getName());
      Channel channel = plugin.getPlayerChannel(player);

      if (channel != null) {
        if (channel.pipeline().get("compress") != null) {
          ChannelHandler handler = channel.pipeline().get("framedimage:encoder");
          channel.pipeline().remove(handler);
          channel.pipeline().addAfter("compress", "framedimage:encoder", handler);
        }

        plugin.spawn(player);
      }
    }, 10L);
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();
    plugin.getPlayerDisplays().remove(player.getName());
    if (player.getLocation().getWorld() == event.getRespawnLocation().getWorld()) {
      plugin.getScheduler().runDelayed(plugin, () -> plugin.spawn(player), 10L);
    }
  }

  @EventHandler
  public void onChangeDimension(PlayerChangedWorldEvent event) {
    Player player = event.getPlayer();
    plugin.getPlayerDisplays().remove(player.getName());
    plugin.spawn(player);
  }
}
