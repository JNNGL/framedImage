/*
 *  Copyright (C) 2022-2026  JNNGL
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

package com.jnngl.framedimage.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.elytrium.java.commons.config.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.jnngl.framedimage.config.Messages;
import org.jetbrains.annotations.NotNull;

public class SubCommandExecutor implements CommandExecutor {
  private final Map<String, SubCommand> subcommands = new HashMap<>();
  private String helpHeader = ChatColor.GOLD + "Help";
  private String permission = null;

  public void register(String name, SubCommand subCommand) {
    subcommands.put(name, subCommand);
  }

  public void unregister(String name) {
    subcommands.remove(name);
  }

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
    if (permission != null && !commandSender.hasPermission(permission)) {
      commandSender.sendMessage(ChatColor.RED + Messages.IMP.MESSAGES.COMMAND.NOT_ENOUGH_PERMISSIONS);
      return true;
    }

    SubCommand subCommand = null;
    if (args.length >= 1) {
      subCommand = subcommands.get(args[0].toLowerCase());
    }

    if (subCommand == null) {
      commandSender.sendMessage(helpHeader.split("\n"));
      subcommands.forEach((name, cmd) -> {
        String help = cmd.help(commandSender);
        if (help != null && !help.isBlank()) {
          commandSender.sendMessage(ChatColor.GOLD + "/" + s + " " + name + " " + ChatColor.DARK_GRAY + help);
        }
      });
    } else {
      List<String> parsedArgs = new ArrayList<>();

      StringBuilder currentString = null;
      for (int i = 1; i < args.length; i++) {
        String arg = args[i];
        String parsedArg = arg.replace("\\\"", "\"");

        if (arg.startsWith("\"") && arg.endsWith("\"") && !arg.endsWith("\\\"")) {
          parsedArgs.add(parsedArg.substring(1, parsedArg.length() - 1));
        } else if (arg.startsWith("\"")) {
          currentString = new StringBuilder(parsedArg.substring(1));
        } else if (arg.endsWith("\"") && !arg.endsWith("\\\"")) {
          if (currentString == null) {
            commandSender.sendMessage(ChatColor.RED +
                Placeholders.replace(Messages.IMP.MESSAGES.COMMAND.COULDNT_PARSE_ARGUMENT, parsedArg));
            return true;
          }

          currentString.append(" ").append(parsedArg, 0, parsedArg.length() - 1);
          parsedArgs.add(currentString.toString());
          currentString = null;
        } else {
          if (currentString != null) {
            currentString.append(" ").append(parsedArg);
          } else {
            parsedArgs.add(parsedArg);
          }
        }
      }

      if (currentString != null) {
        commandSender.sendMessage(ChatColor.RED +
            Placeholders.replace(Messages.IMP.MESSAGES.COMMAND.COULDNT_PARSE_ARGUMENT, currentString));
        return true;
      }

      try {
        if (!subCommand.execute(commandSender, parsedArgs)) {
          String usage = subCommand.usage(commandSender, s);

          if (usage != null && !usage.isBlank()) {
            commandSender.sendMessage(ChatColor.GOLD +
                Placeholders.replace(Messages.IMP.MESSAGES.COMMAND.USAGE, ChatColor.DARK_GRAY + usage));
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        commandSender.sendMessage(ChatColor.RED + e.getClass().getName() + ": " + e.getMessage());
        return true;
      }
    }

    return true;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Map<String, SubCommand> getSubcommands() {
    return subcommands;
  }

  public String getHelpHeader() {
    return helpHeader;
  }

  public void setHelpHeader(String helpHeader) {
    this.helpHeader = helpHeader;
  }
}
