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

package com.jnngl.framedimage;

import com.jnngl.framedimage.protocol.packets.DestroyEntity;
import com.jnngl.framedimage.protocol.packets.MapData;
import com.jnngl.framedimage.protocol.packets.SetMetadata;
import com.jnngl.framedimage.protocol.packets.SpawnEntity;
import com.jnngl.mapcolor.palette.Palette;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import com.jnngl.framedimage.config.Config;
import com.jnngl.framedimage.protocol.Packet;
import com.jnngl.framedimage.protocol.data.Facing;
import com.jnngl.framedimage.protocol.data.ItemFrame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FrameDisplay {

  private static final BlockFace[] OFFSET_FACES = new BlockFace[]{
      BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH
  };

  private static final Facing[] FACINGS = new Facing[]{
      Facing.NORTH, Facing.EAST, Facing.SOUTH, Facing.WEST
  };

  private static int EID_COUNTER = 0;
  private static int MAP_COUNTER = -1;

  private final List<List<Packet>> framePackets;
  private final List<Packet> spawnPackets = new ArrayList<>();
  private final List<Packet> destroyPackets = new ArrayList<>();
  private final Location location;
  private final BlockFace face;
  private final BlockFace offsetFace;
  private final int width;
  private final int height;
  private final List<BufferedImage> frames;
  private final UUID uuid;
  private Iterator<List<Packet>> framePacketsIterator;

  private static BufferedImage resizeIfNeeded(BufferedImage image, int width, int height) {
    if (image.getWidth() != width || image.getHeight() != height) {
      BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

      Graphics2D graphics = resized.createGraphics();
      graphics.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
      graphics.dispose();

      return resized;
    } else {
      return image;
    }
  }

  public FrameDisplay(FramedImage plugin, Location location, BlockFace face,
                      int width, int height, List<BufferedImage> frames, UUID uuid) {
    frames = frames.stream()
        .map(image -> resizeIfNeeded(image, width * 128, height * 128))
        .collect(Collectors.toList());

    this.location = location;
    this.face = face;
    this.width = width;
    this.height = height;
    this.frames = frames;
    this.uuid = uuid;

    framePackets = frames.stream()
        .map(frame -> new ArrayList<Packet>())
        .collect(Collectors.toList());

    offsetFace = OFFSET_FACES[face.ordinal()];
    Facing facing = FACINGS[face.ordinal()];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int blockX = location.getBlockX() + offsetFace.getModX() * x;
        int blockY = location.getBlockY() + y;
        int blockZ = location.getBlockZ() + offsetFace.getModZ() * x;

        int eid = --EID_COUNTER;

        spawnPackets.add(
            new SpawnEntity(
                eid,
                UUID.randomUUID(),
                Config.IMP.GLOW
                    ? ItemFrame::getGlowingID
                    : ItemFrame::getID,
                blockX, blockY, blockZ,
                0, facing.getYaw(), 0,
                facing::getID,
                0, 0, 0
            )
        );

        for (int i = 0; i < frames.size(); i++) {
          BufferedImage frame = frames.get(i);
          BufferedImage part = frame.getSubimage(x * 128, (height - 1 - y) * 128, 128, 128);

          int mapId = --MAP_COUNTER;

          if (Config.IMP.CACHE_MAPS) {
            Map<Palette, byte[]> data = new HashMap<>();
            for (Palette palette : Palette.ALL_PALETTES) {
              data.put(palette, plugin.getColorMatchers().get(palette).matchImage(part));
            }

            spawnPackets.add(new MapData(mapId, (byte) 0, data::get));
          } else {
            spawnPackets.add(
                new MapData(
                    mapId,
                    (byte) 0,
                    palette ->
                        plugin
                            .getColorMatchers()
                            .get(palette)
                            .matchImage(part)
                )
            );
          }

          framePackets.get(i).add(new SetMetadata(eid, version -> ItemFrame.createMapMetadata(version, mapId)));
        }

        destroyPackets.add(new DestroyEntity(eid));
      }
    }

    framePacketsIterator = framePackets.iterator();
  }

  public FrameDisplay(FramedImage plugin, Location location, BlockFace face,
                      int width, int height, List<BufferedImage> frames) {
    this(plugin, location, face, width, height, frames, UUID.randomUUID());
  }

  public List<Packet> getNextFramePackets() {
    synchronized (framePackets) {
      if (!framePacketsIterator.hasNext()) {
        framePacketsIterator = framePackets.iterator();
      }

      return framePacketsIterator.next();
    }
  }

  public int getNumFrames() {
    return frames.size();
  }

  public Location getLocation() {
    return location;
  }

  public BlockFace getFace() {
    return face;
  }

  public BlockFace getOffsetFace() {
    return offsetFace;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public List<BufferedImage> getFrames() {
    return frames;
  }

  public UUID getUUID() {
    return uuid;
  }

  public List<Packet> getSpawnPackets() {
    return spawnPackets;
  }

  public List<Packet> getDestroyPackets() {
    return destroyPackets;
  }
}
