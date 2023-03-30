package com.jnngl.framedimage.scheduler;

import com.jnngl.framedimage.FramedImage;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class FoliaTaskScheduler implements TaskScheduler {

  @Override
  public void runAsync(FramedImage plugin, Runnable task) {
    Bukkit.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
  }

  @Override
  public void runDelayed(FramedImage plugin, Runnable task, long delay) {
    Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> task.run(), delay);
  }

  @Override
  public CancellableTask runAtFixedRate(FramedImage plugin, Location location, Runnable task, long delay, long period) {
    return Bukkit.getServer().getRegionScheduler()
        .runAtFixedRate(plugin, location, scheduledTask -> task.run(), delay, period)::cancel;
  }
}
