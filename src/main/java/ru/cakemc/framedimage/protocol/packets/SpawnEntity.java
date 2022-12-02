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

package ru.cakemc.framedimage.protocol.packets;

import io.netty.buffer.ByteBuf;
import ru.cakemc.framedimage.protocol.IdMapping;
import ru.cakemc.framedimage.protocol.MinecraftVersion;
import ru.cakemc.framedimage.protocol.Packet;
import ru.cakemc.framedimage.protocol.ProtocolUtils;

import java.util.UUID;
import java.util.function.Function;

public class SpawnEntity implements Packet {

  private static final IdMapping ID_MAPPING =
      new IdMapping()
          .add(MinecraftVersion.MINIMUM_VERSION, 0x0E)
          .add(MinecraftVersion.MINECRAFT_1_9, 0x00)
          .build();

  private final int id;
  private final UUID uuid;
  private final Function<MinecraftVersion, Integer> type;
  private final double positionX;
  private final double positionY;
  private final double positionZ;
  private final float pitch;
  private final float yaw;
  private final float headYaw;
  private final Function<MinecraftVersion, Integer> data;
  private final float velocityX;
  private final float velocityY;
  private final float velocityZ;

  public SpawnEntity(int id, UUID uuid, Function<MinecraftVersion, Integer> type, double positionX, double positionY,
                     double positionZ, float pitch, float yaw, float headYaw, Function<MinecraftVersion, Integer> data,
                     float velocityX, float velocityY, float velocityZ) {
    this.id = id;
    this.uuid = uuid;
    this.type = type;
    this.positionX = positionX;
    this.positionY = positionY;
    this.positionZ = positionZ;
    this.pitch = pitch;
    this.yaw = yaw;
    this.headYaw = headYaw;
    this.data = data;
    this.velocityX = velocityX;
    this.velocityY = velocityY;
    this.velocityZ = velocityZ;
  }

  @Override
  public void encode(ByteBuf buf, MinecraftVersion protocolVersion) {
    ProtocolUtils.writeVarInt(buf, id);
    if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_8) > 0) {
      ProtocolUtils.writeUUID(buf, uuid);
      if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_13_2) > 0) {
        ProtocolUtils.writeVarInt(buf, type.apply(protocolVersion));
      } else {
        buf.writeByte(type.apply(protocolVersion));
      }
      buf.writeDouble(positionX);
      buf.writeDouble(positionY);
      buf.writeDouble(positionZ);
    } else {
      buf.writeByte(type.apply(protocolVersion));
      buf.writeInt((int) (positionX * 32.0));
      buf.writeInt((int) (positionY * 32.0));
      buf.writeInt((int) (positionZ * 32.0));
    }
    buf.writeByte((int) (pitch * (256.0F / 360.0F)));
    buf.writeByte((int) (yaw * (256.0F / 360.0F)));
    if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_18_2) > 0) {
      buf.writeByte((int) (headYaw * (256.0F / 360.0F)));
      ProtocolUtils.writeVarInt(buf, data.apply(protocolVersion));
    } else {
      buf.writeInt(data.apply(protocolVersion));
    }
    buf.writeShort((int) (velocityX * 8000.0F));
    buf.writeShort((int) (velocityY * 8000.0F));
    buf.writeShort((int) (velocityZ * 8000.0F));
  }

  @Override
  public int getID(MinecraftVersion version) {
    return ID_MAPPING.getID(version);
  }
}
