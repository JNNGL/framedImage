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

package com.jnngl.framedimage.protocol.data;

import com.jnngl.framedimage.protocol.data.nbt.Nbt;
import com.jnngl.framedimage.protocol.data.nbt.NbtTag;
import io.netty.buffer.ByteBuf;
import com.jnngl.framedimage.protocol.MinecraftVersion;
import com.jnngl.framedimage.protocol.ProtocolUtils;

import java.util.Map;
import java.util.function.Function;

public class EntityMetadata {

  public interface Entry {

    void encode(ByteBuf buf, MinecraftVersion protocolVersion);

    int getType(MinecraftVersion protocolVersion);
  }

  public static class SlotEntry implements Entry {

    private final boolean present;
    private final Function<MinecraftVersion, Integer> item;
    private final int count;
    private final int data;
    private final NbtTag nbt;

    public SlotEntry(boolean present, Function<MinecraftVersion, Integer> item, int count, int data, NbtTag nbt) {
      this.present = present;
      this.item = item;
      this.count = count;
      this.data = data;
      this.nbt = nbt;
    }

    public SlotEntry(Function<MinecraftVersion, Integer> item, int count, int data, NbtTag nbt) {
      this(true, item, count, data, nbt);
    }

    public SlotEntry() {
      this(false, null, 0, 0, null);
    }

    @Override
    public void encode(ByteBuf buf, MinecraftVersion protocolVersion) {
      if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_20_5) >= 0) {
        ProtocolUtils.writeVarInt(buf, 1);
        ProtocolUtils.writeVarInt(buf, item.apply(protocolVersion));
        ProtocolUtils.writeVarInt(buf, 1);
        ProtocolUtils.writeVarInt(buf, 0);
        ProtocolUtils.writeVarInt(buf, 26);
        ProtocolUtils.writeVarInt(buf, this.data);
        return;
      }

      if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_13_2) >= 0) {
        buf.writeBoolean(present);
      }

      if (!present && protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_13_2) < 0) {
        buf.writeShort(-1);
      }

      if (present) {
        if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_13_2) < 0) {
          buf.writeShort(item.apply(protocolVersion));
        } else {
          ProtocolUtils.writeVarInt(buf, item.apply(protocolVersion));
        }
        buf.writeByte(count);
        if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_13) < 0) {
          buf.writeShort(data & ~Short.MIN_VALUE);
        }

        if (nbt == null) {
          if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_8) < 0) {
            buf.writeShort(-1);
          } else {
            buf.writeByte(0);
          }
        } else {
          Nbt.write(buf, nbt, protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_20_2) >= 0);
        }
      }
    }

    @Override
    public int getType(MinecraftVersion protocolVersion) {
      if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_12_2) <= 0) {
        return 5;
      } else if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_19_1) <= 0) {
        return 6;
      } else {
        return 7;
      }
    }
  }

  private final Map<Byte, Entry> entries;

  public EntityMetadata(Map<Byte, Entry> entries) {
    this.entries = entries;
  }

  public void encode(ByteBuf buf, MinecraftVersion protocolVersion) {
    if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_8) <= 0) {
      entries.forEach((index, value) -> {
        buf.writeByte((index & 0x1F) | (value.getType(protocolVersion) << 5));
        value.encode(buf, protocolVersion);
      });
      buf.writeByte(0x7F);
    } else {
      entries.forEach((index, value) -> {
        buf.writeByte(index);
        ProtocolUtils.writeVarInt(buf, value.getType(protocolVersion));
        value.encode(buf, protocolVersion);
      });
      buf.writeByte(0xFF);
    }
  }
}
