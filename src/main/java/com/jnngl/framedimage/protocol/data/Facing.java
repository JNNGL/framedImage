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

import com.jnngl.framedimage.protocol.MinecraftVersion;

public enum Facing {
  DOWN(0, 2, 0),
  UP(0, 2, 0),
  NORTH(2, 2, 180),
  SOUTH(3, 0, 0),
  WEST(4, 1, 90),
  EAST(5, 3, 270);

  final int modernID;
  final int legacyID;
  final float yaw;

  Facing(int modernID, int legacyID, float yaw) {
    this.modernID = modernID;
    this.legacyID = legacyID;
    this.yaw = yaw;
  }

  public int getModernID() {
    return modernID;
  }

  public int getLegacyID() {
    return legacyID;
  }

  public float getYaw() {
    return yaw;
  }

  public int getID(MinecraftVersion version) {
    if (version.compareTo(MinecraftVersion.MINECRAFT_1_13) >= 0) {
      return modernID;
    } else {
      return legacyID;
    }
  }
}
