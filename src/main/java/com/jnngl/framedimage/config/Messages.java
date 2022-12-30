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

public class Messages extends YamlConfig {

  @Ignore
  public static final Messages IMP = new Messages();

  @Create
  public MESSAGES MESSAGES;

  public static class MESSAGES {

    @Create
    public COMMAND COMMAND;

    public static class COMMAND {

      public String NOT_ENOUGH_PERMISSIONS = "Not enough permissions.";
      @Placeholders("{ARGUMENT}")
      public String COULDNT_PARSE_ARGUMENT = "Couldn't parse argument: {ARGUMENT}";
      @Placeholders("{USAGE}")
      public String USAGE = "Usage: {USAGE}";

      @Create
      public CREATE CREATE;

      public static class CREATE {

        public String HELP = "Creates an image on the block where the cursor is pointing. (Frames go right and up)";
        @Placeholders("{COMMAND}")
        public String USAGE = "/{COMMAND} create <width> <height> <url>";
        public String BLOCK_NOT_FOUND = "Can't find block.";
        public String TOO_FEW_ARGUMENTS = "Too few arguments.";
        public String ONLY_PLAYERS_CAN_USE = "Only players can use this command.";
        public String INVALID_ARGUMENTS = "Invalid arguments.";
      }

      @Create
      public REMOVE REMOVE;

      public static class REMOVE {

        public String HELP = "Deletes the image the cursor is pointing to.";
        @Placeholders("{COMMAND}")
        public String USAGE = "/{COMMAND} remove";
        public String BLOCK_NOT_FOUND = "Can't find block.";
        public String ONLY_PLAYERS_CAN_USE = "Only players can use this command.";
        public String IMAGE_NOT_FOUND = "Can't find image.";
      }

      @Create
      public RELOAD RELOAD;

      public static class RELOAD {

        public String HELP = "Reloads the config and images.";
        @Placeholders("{COMMAND}")
        public String USAGE = "/{COMMAND} reload";
        public String RELOADED = "Config and images has been successfully reloaded.";
      }
    }
  }
}
