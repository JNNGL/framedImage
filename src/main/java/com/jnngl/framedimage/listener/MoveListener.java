package com.jnngl.framedimage.listener;

import com.jnngl.framedimage.FramedImage;
import com.jnngl.framedimage.util.SectionUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MoveListener implements Listener {

  private final FramedImage plugin;

  public MoveListener(FramedImage plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    if (event.getFrom().getChunk() == event.getTo().getChunk()) {
      return;
    }

    long fromSection = SectionUtil.getSectionIndex(event.getFrom());
    long toSection = SectionUtil.getSectionIndex(event.getTo());

    if (fromSection == toSection && event.getFrom().getWorld() == event.getTo().getWorld()) {
      return;
    }

    plugin.updateSection(event.getPlayer(), event.getTo().getWorld().getName(), toSection);
  }

  @EventHandler
  public void onTeleport(PlayerTeleportEvent event) {
    onMove(event);
  }
}
