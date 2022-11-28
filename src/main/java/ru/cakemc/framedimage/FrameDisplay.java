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

import com.jnngl.mapcolor.palette.Palette;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import ru.cakemc.framedimage.protocol.Packet;
import ru.cakemc.framedimage.protocol.data.Facing;
import ru.cakemc.framedimage.protocol.data.ItemFrame;
import ru.cakemc.framedimage.protocol.packets.DestroyEntity;
import ru.cakemc.framedimage.protocol.packets.MapData;
import ru.cakemc.framedimage.protocol.packets.SetMetadata;
import ru.cakemc.framedimage.protocol.packets.SpawnEntity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FrameDisplay {

  private static final BlockFace[] OFFSET_FACES = new BlockFace[] {
      BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH
  };

  private static final Facing[] FACINGS = new Facing[] {
      Facing.NORTH, Facing.EAST, Facing.SOUTH, Facing.WEST
  };

  private static int EID_COUNTER = 0;

  private final Location location;
  private final BlockFace face;
  private final BlockFace offsetFace;
  private final int width;
  private final int height;
  private final BufferedImage image;
  private final UUID uuid;

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

  private final List<Packet> spawnPackets = new ArrayList<>();
  private final List<Packet> destroyPackets = new ArrayList<>();

  public FrameDisplay(FramedImage plugin, Location location, BlockFace face,
                      int width, int height, BufferedImage image, UUID uuid) {
    this.location = location;
    this.face = face;
    this.width = width;
    this.height = height;
    this.image = image;
    this.uuid = uuid;

    image = resizeIfNeeded(image, width * 128, height * 128);

    offsetFace = OFFSET_FACES[face.ordinal()];
    Facing facing = FACINGS[face.ordinal()];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        BufferedImage part = image.getSubimage(x * 128, (height - 1 - y) * 128, 128, 128);

        Map<Palette, byte[]> data = new HashMap<>();
        for (Palette palette : Palette.ALL_PALETTES) {
          data.put(palette, plugin.getColorMatchers().get(palette).matchImage(part));
        }

        int blockX = location.getBlockX() + offsetFace.getModX() * x;
        int blockY = location.getBlockY() + y;
        int blockZ = location.getBlockZ() + offsetFace.getModZ() * x;

        int eid = --EID_COUNTER;

        spawnPackets.add(
            new SpawnEntity(
                eid,
                UUID.randomUUID(),
                ItemFrame::getID,
                blockX, blockY, blockZ,
                0, facing.getYaw(), 0,
                facing::getID,
                0, 0, 0
            )
        );

        spawnPackets.add(new SetMetadata(eid,
            version -> ItemFrame.createMapMetadata(version, eid)));

        spawnPackets.add(new MapData(eid, (byte) 0, data));

        destroyPackets.add(new DestroyEntity(eid));
      }
    }
  }

  public FrameDisplay(FramedImage plugin, Location location, BlockFace face,
                      int width, int height, BufferedImage image) {
    this(plugin, location, face, width, height, image, UUID.randomUUID());
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

  public BufferedImage getImage() {
    return image;
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
