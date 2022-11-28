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

package ru.cakemc.framedimage.injection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;

import java.util.List;

public class ChannelInjectionHandler extends ChannelInboundHandlerAdapter {

  private final List<ChannelInjector> injectors;

  public ChannelInjectionHandler(List<ChannelInjector> injectors) {
    this.injectors = injectors;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Channel channel = (Channel) msg;

    channel.pipeline().addLast(new ChannelInitializer<>() {

      @Override
      protected void initChannel(Channel ch) {
        channel.pipeline().addLast(new ChannelDuplexHandler() {

          @Override
          public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ctx.pipeline().remove(this);
            inject(ctx.channel());

            super.channelRead(ctx, msg);
          }

          @Override
          public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            ctx.pipeline().remove(this);
            inject(ctx.channel());

            super.write(ctx, msg, promise);
          }
        });
      }
    });

    super.channelRead(ctx, msg);
  }

  private void inject(Channel channel) {
    injectors.forEach(injector -> injector.inject(channel));
  }
}
