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
import com.jnngl.framedimage.protocol.data.EntityMetadata;

import java.util.function.Function;

public class SetMetadata implements Packet {

  private static final IdMapping ID_MAPPING =
      new IdMapping()
          .add(MinecraftVersion.MINIMUM_VERSION, 0x1C)
          .add(MinecraftVersion.MINECRAFT_1_9, 0x39)
          .add(MinecraftVersion.MINECRAFT_1_12, 0x3B)
          .add(MinecraftVersion.MINECRAFT_1_12_1, 0x3C)
          .add(MinecraftVersion.MINECRAFT_1_14, 0x43)
          .add(MinecraftVersion.MINECRAFT_1_15, 0x44)
          .add(MinecraftVersion.MINECRAFT_1_17, 0x4D)
          .add(MinecraftVersion.MINECRAFT_1_19_1, 0x50)
          .add(MinecraftVersion.MINECRAFT_1_19_3, 0x4E)
          .add(MinecraftVersion.MINECRAFT_1_19_4, 0x52)
          .add(MinecraftVersion.MINECRAFT_1_20_2, 0x54)
          .add(MinecraftVersion.MINECRAFT_1_20_3, 0x56)
          .add(MinecraftVersion.MINECRAFT_1_20_5, 0x58)
          .build();

  private final int entityId;
  private final Function<MinecraftVersion, EntityMetadata> metadata;

  public SetMetadata(int entityId, Function<MinecraftVersion, EntityMetadata> metadata) {
    this.entityId = entityId;
    this.metadata = metadata;
  }

  @Override
  public void encode(ByteBuf buf, MinecraftVersion protocolVersion) {
    if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_7_6) <= 0) {
      buf.writeInt(this.entityId);
    } else {
      ProtocolUtils.writeVarInt(buf, this.entityId);
    }
    this.metadata.apply(protocolVersion).encode(buf, protocolVersion);
  }

  @Override
  public int getID(MinecraftVersion version) {
    return ID_MAPPING.getID(version);
  }
}
