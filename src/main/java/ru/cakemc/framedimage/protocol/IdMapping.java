package ru.cakemc.framedimage.protocol;

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
