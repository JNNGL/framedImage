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
import com.jnngl.framedimage.protocol.ProtocolUtils;
import org.jetbrains.annotations.NotNull;

public class HandshakeListener extends ChannelInboundHandlerAdapter {

  private final FramedImage plugin;

  public HandshakeListener(FramedImage plugin) {
    this.plugin = plugin;
  }

  @Override
  public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
    if (msg instanceof ByteBuf buf) {
      int ridx = buf.readerIndex();

      if (ProtocolUtils.readVarInt(buf) == 0) {
        int protocol = ProtocolUtils.readVarInt(buf);
        LoginListener loginListener = new LoginListener(plugin, protocol);

        ProtocolUtils.readString(buf);
        buf.readShort();

        if (ProtocolUtils.readVarInt(buf) == 2) {
          ctx.pipeline().addBefore("framedimage:handshake", "framedimage:login", loginListener);
        }

        ctx.pipeline().remove(this);
      }

      buf.readerIndex(ridx);
    }

    super.channelRead(ctx, msg);
  }
}
