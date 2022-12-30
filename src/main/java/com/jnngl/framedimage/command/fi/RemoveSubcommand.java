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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.jnngl.framedimage.command.SubCommand;
import com.jnngl.framedimage.config.Messages;
import com.jnngl.framedimage.util.BlockUtil;

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

    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage(ChatColor.RED + Messages.IMP.MESSAGES.COMMAND.REMOVE.ONLY_PLAYERS_CAN_USE);
      return true;
    }

    Player player = (Player) commandSender;

    BlockFace blockFace = BlockUtil.getBlockFace(player.getLocation().getYaw());

    Block block = player.getTargetBlock(null, 100);
    if (block == null || block.isEmpty()) {
      commandSender.sendMessage(ChatColor.RED + Messages.IMP.MESSAGES.COMMAND.REMOVE.BLOCK_NOT_FOUND);
      return true;
    }

    Location targetLocation = BlockUtil.getNextBlockLocation(block.getLocation(), blockFace);
    List<FrameDisplay> displays = plugin.getDisplays().get(targetLocation.getWorld().getName());
    if (displays == null) {
      commandSender.sendMessage(ChatColor.RED + Messages.IMP.MESSAGES.COMMAND.REMOVE.IMAGE_NOT_FOUND);
      return true;
    }

    FrameDisplay targetDisplay = null;
    for (FrameDisplay display : displays) {
      Location location = display.getLocation();
      int width = display.getWidth();
      int height = display.getHeight();
      BlockFace offsetFace = display.getOffsetFace();

      int x1 = location.getBlockX();
      int y1 = location.getBlockY();
      int z1 = location.getBlockZ();

      int x2 = x1 + width * offsetFace.getModX();
      int y2 = y1 + height;
      int z2 = z1 + width * offsetFace.getModZ();

      if (
          targetLocation.getBlockX() >= Math.min(x1, x2) &&
          targetLocation.getBlockX() <= Math.max(x1, x2) &&
          targetLocation.getBlockY() >= Math.min(y1, y2) &&
          targetLocation.getBlockY() <= Math.max(y1, y2) &&
          targetLocation.getBlockZ() >= Math.min(z1, z2) &&
          targetLocation.getBlockZ() <= Math.max(z1, z2)
      ) {
        targetDisplay = display;
        break;
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
