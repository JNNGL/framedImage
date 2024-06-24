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

import io.netty.buffer.ByteBuf;
import com.jnngl.framedimage.protocol.MinecraftVersion;
import com.jnngl.framedimage.protocol.Packet;
import com.jnngl.framedimage.protocol.IdMapping;
import com.jnngl.framedimage.protocol.ProtocolUtils;

public class DestroyEntity implements Packet {

  private static final IdMapping ID_MAPPING =
      new IdMapping()
          .add(MinecraftVersion.MINIMUM_VERSION, 0x13)
          .add(MinecraftVersion.MINECRAFT_1_9, 0x30)
          .add(MinecraftVersion.MINECRAFT_1_12, 0x31)
          .add(MinecraftVersion.MINECRAFT_1_12_1, 0x32)
          .add(MinecraftVersion.MINECRAFT_1_13, 0x35)
          .add(MinecraftVersion.MINECRAFT_1_14, 0x37)
          .add(MinecraftVersion.MINECRAFT_1_15, 0x38)
          .add(MinecraftVersion.MINECRAFT_1_16, 0x36)
          .add(MinecraftVersion.MINECRAFT_1_17, 0x3A)
          .add(MinecraftVersion.MINECRAFT_1_19, 0x38)
          .add(MinecraftVersion.MINECRAFT_1_19_1, 0x3B)
          .add(MinecraftVersion.MINECRAFT_1_19_3, 0x3A)
          .add(MinecraftVersion.MINECRAFT_1_19_4, 0x3E)
          .add(MinecraftVersion.MINECRAFT_1_20_2, 0x40)
          .add(MinecraftVersion.MINECRAFT_1_20_5, 0x42)
          .build();

  private final int entity;

  public DestroyEntity(int entity) {
    this.entity = entity;
  }

  @Override
  public void encode(ByteBuf buf, MinecraftVersion version) {
    if (version.compareTo(MinecraftVersion.MINECRAFT_1_17) != 0) {
      buf.writeByte(1);
    }

    if (version.compareTo(MinecraftVersion.MINECRAFT_1_8) >= 0) {
      ProtocolUtils.writeVarInt(buf, entity);
    } else {
      buf.writeInt(entity);
    }
  }

  @Override
  public int getID(MinecraftVersion version) {
    return ID_MAPPING.getID(version);
  }
}
