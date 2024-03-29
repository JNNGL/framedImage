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

package com.jnngl.framedimage.config;

import net.elytrium.java.commons.config.YamlConfig;

public class Config extends YamlConfig {

  @Ignore
  public static final Config IMP = new Config();

  public boolean DITHERING = true;
  public boolean GLOW = false;
  public boolean CACHE_MAPS = true;

  @Create
  public DYNAMIC_FRAME_SPAWN DYNAMIC_FRAME_SPAWN;

  public static class DYNAMIC_FRAME_SPAWN {

    @Comment("Frames will only be sent when the player gets closer to them.")
    public boolean ENABLED = true;
    public int SECTION_SHIFT = 7;
    public int GRID_SIZE = 3;
  }
}
