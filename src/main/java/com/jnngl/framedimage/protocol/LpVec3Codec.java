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

public final class LpVec3Codec {

  private static final int SCALE_MASK = 0x03;
  private static final int CONTINUATION_FLAG = 0x04;
  private static final double MAX_QUANTIZED_VALUE = 32766.0;
  private static final double ABS_MIN_VALUE = 3.051944088384301E-5;
  private static final double ABS_MAX_VALUE = 1.7179869183E10;

  private LpVec3Codec() {
  }

  public static void write(ByteBuf buf, double x, double y, double z) {
    double sanitizedX = sanitizeVelocity(x);
    double sanitizedY = sanitizeVelocity(y);
    double sanitizedZ = sanitizeVelocity(z);

    double maxAbs = Math.max(Math.abs(sanitizedX), Math.max(Math.abs(sanitizedY), Math.abs(sanitizedZ)));
    if (maxAbs < ABS_MIN_VALUE) {
      buf.writeByte(0);
      return;
    }

    int scale = (int) Math.ceil(maxAbs);
    boolean needsContinuation = (scale & SCALE_MASK) != scale;
    int scaleByte = needsContinuation
        ? ((scale & SCALE_MASK) | CONTINUATION_FLAG)
        : (scale & SCALE_MASK);

    long packedX = packVelocityComponent(sanitizedX / scale);
    long packedY = packVelocityComponent(sanitizedY / scale);
    long packedZ = packVelocityComponent(sanitizedZ / scale);

    long low32 = (long) scaleByte | (packedX << 3) | (packedY << 18);
    int high16 = (int) (((packedY >> 14) & 0x01L) | (packedZ << 1));

    buf.writeIntLE((int) low32);
    buf.writeShortLE(high16);

    if (needsContinuation) {
      ProtocolUtils.writeVarInt(buf, scale >> 2);
    }
  }

  private static double sanitizeVelocity(double value) {
    if (Double.isNaN(value)) {
      return 0.0;
    }

    return Math.max(-ABS_MAX_VALUE, Math.min(value, ABS_MAX_VALUE));
  }

  private static long packVelocityComponent(double value) {
    return Math.round((value * 0.5 + 0.5) * MAX_QUANTIZED_VALUE);
  }
}
