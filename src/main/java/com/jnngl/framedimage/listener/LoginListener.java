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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.jnngl.framedimage.protocol.MinecraftVersion;
import com.jnngl.framedimage.protocol.PacketEncoder;
import com.jnngl.framedimage.protocol.ProtocolUtils;

public class LoginListener extends ChannelInboundHandlerAdapter {

  private final FramedImage plugin;
  private final int protocol;

  public LoginListener(FramedImage plugin, int protocol) {
    this.plugin = plugin;
    this.protocol = protocol;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof ByteBuf) {
      ByteBuf buf = (ByteBuf) msg;
      int ridx = buf.readerIndex();

      if (ProtocolUtils.readVarInt(buf) == 0) {
        String name = ProtocolUtils.readString(buf);
        MinecraftVersion version = MinecraftVersion.fromPVN(protocol);

        plugin.getLoggingPlayers().add(name);
        plugin.getPlayerChannels().put(name, ctx.channel());

        plugin.getLogger().info(name + " has connected with protocol " + protocol + " (" + version.getVersionName() + ")");

        ctx.pipeline().addAfter("prepender", "framedimage:encoder", new PacketEncoder(version));
        ctx.pipeline().addFirst("framedimage:disconnect", new DisconnectHandler(plugin, name));

        ctx.pipeline().remove(this);
      }

      buf.readerIndex(ridx);
    }

    super.channelRead(ctx, msg);
  }
}
