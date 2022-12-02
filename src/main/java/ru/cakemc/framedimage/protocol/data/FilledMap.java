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

package ru.cakemc.framedimage.protocol.data;

import ru.cakemc.framedimage.protocol.IdMapping;
import ru.cakemc.framedimage.protocol.MinecraftVersion;

public class FilledMap {

  private static final IdMapping ID_MAPPING =
      new IdMapping()
          .add(MinecraftVersion.MINIMUM_VERSION, 358)
          .add(MinecraftVersion.MINECRAFT_1_13, 608)
          .add(MinecraftVersion.MINECRAFT_1_13_2, 613)
          .add(MinecraftVersion.MINECRAFT_1_14, 671)
          .add(MinecraftVersion.MINECRAFT_1_16, 733)
          .add(MinecraftVersion.MINECRAFT_1_17, 847)
          .add(MinecraftVersion.MINECRAFT_1_19, 886)
          .build();

  public static int getID(MinecraftVersion version) {
    return ID_MAPPING.getID(version);
  }

}
