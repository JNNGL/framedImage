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

package com.jnngl.framedimage.protocol;

import java.util.HashMap;
import java.util.Map;

public class IdMapping {
  
  private final int[] ids = new int[MinecraftVersion.values().length];
  private Map<MinecraftVersion, Integer> tempIDs = new HashMap<>();
  
  public IdMapping add(MinecraftVersion version, int id) {
    tempIDs.put(version, id);
    return this;
  }

  public IdMapping build() {
    int lastID = -1;
    for (MinecraftVersion version : MinecraftVersion.values()) {
      ids[version.ordinal()] = lastID = tempIDs.getOrDefault(version, lastID);
    }

    tempIDs = null;
    return this;
  }
  
  public int getID(MinecraftVersion version) {
    return ids[version.ordinal()];
  }
}
