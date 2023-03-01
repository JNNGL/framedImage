package com.jnngl.framedimage.util;

import com.jnngl.framedimage.config.Config;
import org.bukkit.Location;

public class SectionUtil {

  public static long getSectionIndex(int x, int z) {
    int sectionShift = Config.IMP.DYNAMIC_FRAME_SPAWN.SECTION_SHIFT;
    int shiftedX = x >>> sectionShift;
    int shiftedZ = z >>> sectionShift;
    return (long) shiftedZ << 32 | shiftedX;
  }

  public static long getSectionIndex(Location location) {
    return getSectionIndex(location.getBlockX(), location.getBlockZ());
  }
}
