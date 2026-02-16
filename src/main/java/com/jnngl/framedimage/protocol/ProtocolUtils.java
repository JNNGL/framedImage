/*
 *  Copyright (C) 2022-2026  JNNGL
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

package com.jnngl.framedimage.protocol;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ProtocolUtils {

  private static final int SEGMENT_BITS = 0x7F;
  private static final int CONTINUE_BIT = 0x80;

  public static String readString(ByteBuf buf) {
    byte[] bytes = new byte[readVarInt(buf)];
    buf.readBytes(bytes);
    return new String(bytes, StandardCharsets.UTF_8);
  }

  public static int readVarInt(ByteBuf buf) {
    int value = 0;
    int position = 0;
    byte currentByte;

    while (true) {
      currentByte = buf.readByte();
      value |= (currentByte & SEGMENT_BITS) << position;

      if ((currentByte & CONTINUE_BIT) == 0) break;

      position += 7;

      if (position >= 32) throw new RuntimeException("VarInt is too big");
    }

    return value;
  }

  public static void writeVarInt(ByteBuf buf, int value) {
    while (true) {
      if ((value & ~SEGMENT_BITS) == 0) {
        buf.writeByte(value);
        return;
      }

      buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
      value >>>= 7;
    }
  }

  public static void writeUUID(ByteBuf buf, UUID uuid) {
    buf.writeLong(uuid.getMostSignificantBits());
    buf.writeLong(uuid.getLeastSignificantBits());
  }
}
