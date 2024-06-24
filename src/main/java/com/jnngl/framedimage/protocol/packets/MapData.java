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

package com.jnngl.framedimage.protocol.packets;

import com.jnngl.mapcolor.palette.Palette;
import io.netty.buffer.ByteBuf;
import com.jnngl.framedimage.protocol.MinecraftVersion;
import com.jnngl.framedimage.protocol.Packet;
import com.jnngl.framedimage.protocol.IdMapping;
import com.jnngl.framedimage.protocol.ProtocolUtils;

import java.util.function.Function;

public class MapData implements Packet {

  private static final IdMapping ID_MAPPING =
      new IdMapping()
          .add(MinecraftVersion.MINIMUM_VERSION, 0x34)
          .add(MinecraftVersion.MINECRAFT_1_9, 0x24)
          .add(MinecraftVersion.MINECRAFT_1_13, 0x26)
          .add(MinecraftVersion.MINECRAFT_1_15, 0x27)
          .add(MinecraftVersion.MINECRAFT_1_16, 0x26)
          .add(MinecraftVersion.MINECRAFT_1_16_2, 0x25)
          .add(MinecraftVersion.MINECRAFT_1_17, 0x27)
          .add(MinecraftVersion.MINECRAFT_1_19, 0x24)
          .add(MinecraftVersion.MINECRAFT_1_19_1, 0x26)
          .add(MinecraftVersion.MINECRAFT_1_19_3, 0x25)
          .add(MinecraftVersion.MINECRAFT_1_19_4, 0x29)
          .add(MinecraftVersion.MINECRAFT_1_20_2, 0x2A)
          .add(MinecraftVersion.MINECRAFT_1_20_5, 0x2C)
          .build();

  private final int mapID;
  private final byte scale;
  private final int columns;
  private final int rows;
  private final int posX;
  private final int posY;
  private final Function<Palette, byte[]> data;

  public MapData(int mapID, byte scale, int columns, int rows, int posX, int posY, Function<Palette, byte[]> data) {
    this.mapID = mapID;
    this.scale = scale;
    this.columns = columns;
    this.rows = rows;
    this.posX = posX;
    this.posY = posY;
    this.data = data;
  }

  public MapData(int mapID, byte scale, int posX, Function<Palette, byte[]> data) {
    this(mapID, scale, 128, 128, posX, 0, data);
  }

  public MapData(int mapID, byte scale, Function<Palette, byte[]> data) {
    this(mapID, scale, 0, data);
  }

  @Override
  public void encode(ByteBuf buf, MinecraftVersion version) {
    byte[] data = this.data.apply(Palette.getPaletteForProtocol(version.getProtocolVersion()));

    if (version.compareTo(MinecraftVersion.MINECRAFT_1_12_2) <= 0) {
      ProtocolUtils.writeVarInt(buf, mapID & ~Short.MIN_VALUE);
    } else {
      ProtocolUtils.writeVarInt(buf, mapID);
    }

    if (version.compareTo(MinecraftVersion.MINECRAFT_1_8) < 0) {
      buf.writeShort(data.length + 3);
      buf.writeByte(0);
      buf.writeByte(posX);
      buf.writeByte(posY);

      buf.writeBytes(data);
    } else {
      buf.writeByte(this.scale);

      if (version.compareTo(MinecraftVersion.MINECRAFT_1_9) >= 0 && version.compareTo(MinecraftVersion.MINECRAFT_1_17) < 0) {
        buf.writeBoolean(false);
      }

      if (version.compareTo(MinecraftVersion.MINECRAFT_1_14) >= 0) {
        buf.writeBoolean(false);
      }

      if (version.compareTo(MinecraftVersion.MINECRAFT_1_17) >= 0) {
        buf.writeBoolean(false);
      } else {
        ProtocolUtils.writeVarInt(buf, 0);
      }

      buf.writeByte(columns);
      buf.writeByte(rows);
      buf.writeByte(posX);
      buf.writeByte(posY);

      ProtocolUtils.writeVarInt(buf, data.length);
      buf.writeBytes(data);
    }
  }

  @Override
  public int getID(MinecraftVersion version) {
    return ID_MAPPING.getID(version);
  }
}
