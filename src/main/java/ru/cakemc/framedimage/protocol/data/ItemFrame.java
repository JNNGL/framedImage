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

import ru.cakemc.framedimage.protocol.MinecraftVersion;
import ru.cakemc.framedimage.protocol.data.nbt.TagCompound;
import ru.cakemc.framedimage.protocol.data.nbt.TagInt;

import java.util.Collections;
import java.util.Map;

public class ItemFrame {

  public static int getID(MinecraftVersion protocolVersion) {
    if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_13_2) <= 0) {
      return 71;
    } else if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_14_4) <= 0) {
      return 35;
    } else if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_15_2) <= 0) {
      return 36;
    } else if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_16_4) <= 0) {
      return 38;
    } else if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_18_2) <= 0) {
      return 42;
    } else {
      return 45;
    }
  }

  public static byte getMetadataIndex(MinecraftVersion protocolVersion) {
    if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_7_6) <= 0) {
      return 2;
    } else if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_8) <= 0) {
      return 8;
    } else if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_9_4) <= 0) {
      return 5;
    } else if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_13_2) <= 0) {
      return 6;
    } else if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_16_4) <= 0) {
      return 7;
    } else {
      return 8;
    }
  }

  public static EntityMetadata createMapMetadata(MinecraftVersion protocolVersion, int mapId) {
    if (protocolVersion.compareTo(MinecraftVersion.MINECRAFT_1_12_2) <= 0) {
      return new EntityMetadata(
          Map.of(
              getMetadataIndex(protocolVersion),
              new EntityMetadata.SlotEntry(FilledMap::getID, 1, mapId, null)
          )
      );
    } else {
      return new EntityMetadata(
          Map.of(
              getMetadataIndex(protocolVersion),
              new EntityMetadata.SlotEntry(FilledMap::getID, 1, 0,
                  new TagCompound("",
                      Collections.singleton(new TagInt("map", mapId))
                  )
              )
          )
      );
    }
  }
}
