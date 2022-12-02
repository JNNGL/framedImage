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
import ru.cakemc.framedimage.protocol.data.nbt.TagCompound;
import ru.cakemc.framedimage.protocol.data.nbt.TagInt;

import java.util.Collections;
import java.util.Map;

public class ItemFrame {

  private static final IdMapping ID_MAPPING =
      new IdMapping()
          .add(MinecraftVersion.MINIMUM_VERSION, 71)
          .add(MinecraftVersion.MINECRAFT_1_14, 35)
          .add(MinecraftVersion.MINECRAFT_1_15, 36)
          .add(MinecraftVersion.MINECRAFT_1_16, 38)
          .add(MinecraftVersion.MINECRAFT_1_17, 42)
          .add(MinecraftVersion.MINECRAFT_1_19, 45)
          .build();

  private static final IdMapping METADATA_INDEX_MAPPING =
      new IdMapping()
          .add(MinecraftVersion.MINIMUM_VERSION, 2)
          .add(MinecraftVersion.MINECRAFT_1_8, 8)
          .add(MinecraftVersion.MINECRAFT_1_9, 5)
          .add(MinecraftVersion.MINECRAFT_1_10, 6)
          .add(MinecraftVersion.MINECRAFT_1_14, 7)
          .add(MinecraftVersion.MINECRAFT_1_17, 8)
          .build();

  public static int getID(MinecraftVersion protocolVersion) {
    return ID_MAPPING.getID(protocolVersion);
  }

  public static byte getMetadataIndex(MinecraftVersion protocolVersion) {
    return (byte) METADATA_INDEX_MAPPING.getID(protocolVersion);
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
