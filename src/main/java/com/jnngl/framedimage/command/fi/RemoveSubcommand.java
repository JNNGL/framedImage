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

package com.jnngl.framedimage.command.fi;

import com.jnngl.framedimage.FrameDisplay;
import com.jnngl.framedimage.FramedImage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.jnngl.framedimage.command.SubCommand;
import com.jnngl.framedimage.config.Messages;
import org.bukkit.util.Vector;

import java.util.List;

public class RemoveSubcommand implements SubCommand {

  private final FramedImage plugin;

  public RemoveSubcommand(FramedImage plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean execute(CommandSender commandSender, List<String> args) {
    if (!commandSender.hasPermission("framedimage.subcommand.remove")) {
      commandSender.sendMessage(ChatColor.RED + Messages.IMP.MESSAGES.COMMAND.NOT_ENOUGH_PERMISSIONS);
      return true;
    }

    if (!(commandSender instanceof Player player)) {
      commandSender.sendMessage(ChatColor.RED + Messages.IMP.MESSAGES.COMMAND.REMOVE.ONLY_PLAYERS_CAN_USE);
      return true;
    }

    List<FrameDisplay> displays = plugin.getDisplays().get(player.getWorld().getName());
    if (displays == null) {
      commandSender.sendMessage(ChatColor.RED + Messages.IMP.MESSAGES.COMMAND.REMOVE.IMAGE_NOT_FOUND);
      return true;
    }

    Vector origin = player.getEyeLocation().toVector();
    Vector direction = player.getLocation().getDirection();

    FrameDisplay targetDisplay = null;
    double nearest = Double.MAX_VALUE;

    for (FrameDisplay display : displays) {
      Location location = display.getLocation();
      if (location.distanceSquared(player.getLocation()) > 50 * 50) {
        continue;
      }

      int width = display.getWidth();
      int height = display.getHeight();
      BlockFace offsetFace = display.getOffsetFace();

      Vector offsetVector = switch(offsetFace) {
        case SOUTH -> new Vector(1, 0, 0);
        case NORTH -> new Vector(0, 0, 1);
        case WEST -> new Vector(1, 0, 1);
        default -> new Vector();
      };

      Vector point1 = location.toVector().add(offsetVector);
      Vector point2 = location.toVector().add(
          new Vector(
              width * offsetFace.getModX() + -0.1 * offsetFace.getModZ(),
              height,
              width * offsetFace.getModZ() + 0.1 * offsetFace.getModX()
          )
      ).add(offsetVector);

      Vector min = Vector.getMinimum(point1, point2);
      point2 = Vector.getMaximum(point1, point2);
      point1 = min;

      point1.subtract(origin).divide(direction);
      point2.subtract(origin).divide(direction);
      Vector near = Vector.getMinimum(point1, point2);
      Vector far = Vector.getMaximum(point1, point2);
      double nearDistance = Math.max(Math.max(near.getX(), near.getY()), near.getZ());
      double farDistance = Math.min(Math.min(far.getX(), far.getY()), far.getZ());

      if (nearDistance <= farDistance && nearDistance < nearest) {
        targetDisplay = display;
        nearest = nearDistance;
      }
    }

    if (targetDisplay == null) {
      commandSender.sendMessage(ChatColor.RED + Messages.IMP.MESSAGES.COMMAND.REMOVE.IMAGE_NOT_FOUND);
      return true;
    }

    plugin.remove(targetDisplay);
    plugin.saveFrames();

    return true;
  }

  @Override
  public String help(CommandSender commandSender) {
    return Messages.IMP.MESSAGES.COMMAND.REMOVE.HELP;
  }

  @Override
  public String usage(CommandSender commandSender, String name) {
    return Messages.IMP.MESSAGES.COMMAND.REMOVE.USAGE;
  }
}
