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

import com.jnngl.framedimage.FramedImage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.jnngl.framedimage.command.SubCommand;
import com.jnngl.framedimage.config.Messages;

import java.util.List;

public class ReloadSubcommand implements SubCommand {

  private final FramedImage plugin;

  public ReloadSubcommand(FramedImage plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean execute(CommandSender commandSender, List<String> args) {
    if (!commandSender.hasPermission("framedimage.subcommand.reload")) {
      commandSender.sendMessage(ChatColor.RED + Messages.IMP.MESSAGES.COMMAND.NOT_ENOUGH_PERMISSIONS);
      return true;
    }

    plugin.reload();
    commandSender.sendMessage(ChatColor.GOLD + Messages.IMP.MESSAGES.COMMAND.RELOAD.RELOADED);
    return true;
  }

  @Override
  public String help(CommandSender commandSender) {
    return Messages.IMP.MESSAGES.COMMAND.RELOAD.HELP;
  }

  @Override
  public String usage(CommandSender commandSender, String name) {
    return Messages.IMP.MESSAGES.COMMAND.RELOAD.USAGE;
  }
}
