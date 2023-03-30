package com.jnngl.framedimage.scheduler;

import com.jnngl.framedimage.FramedImage;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitTaskScheduler implements TaskScheduler {

  @Override
  public void runAsync(FramedImage plugin, Runnable task) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
  }

  @Override
  public void runDelayed(FramedImage plugin, Runnable task, long delay) {
    Bukkit.getScheduler().runTaskLater(plugin, task, delay);
  }

  @Override
  public CancellableTask runAtFixedRate(FramedImage plugin, Location location, Runnable task, long delay, long period) {
    return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)::cancel;
  }
}
