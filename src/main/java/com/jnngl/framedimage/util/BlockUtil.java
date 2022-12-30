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

package com.jnngl.framedimage.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class BlockUtil {

  private static final BlockFace[] AXIS = new BlockFace[] {
      BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
  };

  public static BlockFace getBlockFace(float entityYaw) {
    return AXIS[Math.round(entityYaw / 90.0F) & 0x03];
  }

  public static Location getNextBlockLocation(Location location, BlockFace blockFace) {
    return new Location(
        location.getWorld(),
        location.getBlockX() + blockFace.getModX(),
        location.getBlockY() + blockFace.getModY(),
        location.getBlockZ() + blockFace.getModZ()
    );
  }
}
