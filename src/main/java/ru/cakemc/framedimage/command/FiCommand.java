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

package ru.cakemc.framedimage.command;

import org.bukkit.ChatColor;
import ru.cakemc.framedimage.FramedImage;
import ru.cakemc.framedimage.command.fi.CreateSubcommand;
import ru.cakemc.framedimage.command.fi.ReloadSubcommand;
import ru.cakemc.framedimage.command.fi.RemoveSubcommand;

public class FiCommand extends SubCommandExecutor {

  public FiCommand(FramedImage plugin) {
    setPermission("framedimage.command");
    setHelpHeader(ChatColor.GOLD + "framedImage v" + plugin.getDescription().getVersion());
    register("create", new CreateSubcommand(plugin));
    register("remove", new RemoveSubcommand(plugin));
    register("reload", new ReloadSubcommand(plugin));
  }
}
