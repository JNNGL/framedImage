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

package com.jnngl.framedimage.listener;

import com.jnngl.framedimage.FramedImage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DisconnectHandler extends ChannelInboundHandlerAdapter {

  private final FramedImage plugin;
  private final String name;

  public DisconnectHandler(FramedImage plugin, String name) {
    this.plugin = plugin;
    this.name = name;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    plugin.getLoggingPlayers().remove(name);
    plugin.getPlayerChannels().remove(name);
    plugin.getPlayerDisplays().remove(name);
    super.channelInactive(ctx);
  }
}
