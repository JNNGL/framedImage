package com.jnngl.framedimage.scheduler;

import com.jnngl.framedimage.FramedImage;
import org.bukkit.Location;

public interface TaskScheduler {

  void runAsync(FramedImage plugin, Runnable task);

  void runDelayed(FramedImage plugin, Runnable task, long delay);

  default void runDelayed(FramedImage plugin, Runnable task) {
    runDelayed(plugin, task, 1);
  }

  CancellableTask runAtFixedRate(FramedImage plugin, Location location, Runnable task, long delay, long period);
}
