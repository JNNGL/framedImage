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

import com.jnngl.framedimage.protocol.data.nbt.TagCompound;
import com.jnngl.framedimage.protocol.data.nbt.TagInt;
import com.jnngl.framedimage.protocol.IdMapping;
import com.jnngl.framedimage.protocol.MinecraftVersion;

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
          .add(MinecraftVersion.MINECRAFT_1_19_3, 46)
          .add(MinecraftVersion.MINECRAFT_1_19_4, 56)
          .add(MinecraftVersion.MINECRAFT_1_20_3, 57)
          .add(MinecraftVersion.MINECRAFT_1_20_5, 60)
          .build();

  private static final IdMapping GLOWING_ID_MAPPING =
      new IdMapping()
          .add(MinecraftVersion.MINIMUM_VERSION, 71)
          .add(MinecraftVersion.MINECRAFT_1_14, 35)
          .add(MinecraftVersion.MINECRAFT_1_15, 36)
          .add(MinecraftVersion.MINECRAFT_1_16, 38)
          .add(MinecraftVersion.MINECRAFT_1_17, 32)
          .add(MinecraftVersion.MINECRAFT_1_19, 35)
          .add(MinecraftVersion.MINECRAFT_1_19_3, 36)
          .add(MinecraftVersion.MINECRAFT_1_19_4, 43)
          .add(MinecraftVersion.MINECRAFT_1_20_3, 44)
          .add(MinecraftVersion.MINECRAFT_1_20_5, 47)
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

  public static int getGlowingID(MinecraftVersion protocolVersion) {
    return GLOWING_ID_MAPPING.getID(protocolVersion);
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
              new EntityMetadata.SlotEntry(FilledMap::getID, 1, mapId,
                  new TagCompound("",
                      Collections.singleton(new TagInt("map", mapId))
                  )
              )
          )
      );
    }
  }
}
