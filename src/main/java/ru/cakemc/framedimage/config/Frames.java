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

package ru.cakemc.framedimage.config;

import net.elytrium.java.commons.config.YamlConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import ru.cakemc.framedimage.FrameDisplay;
import ru.cakemc.framedimage.FramedImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Frames extends YamlConfig {

  @Ignore
  public static final Frames IMP = new Frames();

  public Map<String, FrameNode> FRAMES = new HashMap<>();

  @Ignore
  public final Object MUTEX = new Object();

  @NodeSequence
  public static class FrameNode {

    public String WORLD;
    public int X;
    public int Y;
    public int Z;
    public int WIDTH;
    public int HEIGHT;
    public String FACE;
    public String IMAGE = ""; // For backward compatibility
    public List<String> FRAMES;

    public FrameNode(FrameDisplay display, File dataFolder) throws IOException {
      Location location = display.getLocation();
      WORLD = location.getWorld().getName();
      X = location.getBlockX();
      Y = location.getBlockY();
      Z = location.getBlockZ();
      WIDTH = display.getWidth();
      HEIGHT = display.getHeight();
      FACE = display.getFace().toString();
      FRAMES = new ArrayList<>();

      for (int i = 0; i < display.getNumFrames(); i++) {
        Path path = Path.of(dataFolder.getPath(), "images/" + display.getUUID() + "_" + i + ".png");
        File file = path.toFile();

        if (Files.notExists(path)) {
          Files.createDirectories(path.getParent());
          ImageIO.write(display.getFrames().get(i), "PNG", file);
        }

        FRAMES.add(file.getPath().replace(File.separatorChar, '/'));
      }
    }

    public FrameNode() {

    }

    public FrameDisplay createFrameDisplay(FramedImage plugin, UUID uuid) throws IOException {
      Location location = new Location(
          Bukkit.getWorld(WORLD),
          X, Y, Z
      );

      BlockFace face = BlockFace.valueOf(FACE);

      List<BufferedImage> frames;
      if (IMAGE != null && !IMAGE.isBlank()) {
        frames = Collections.singletonList(ImageIO.read(new File(IMAGE)));
      } else {
        frames = new ArrayList<>();
        for (String frame : FRAMES) {
          frames.add(ImageIO.read(new File(frame)));
        }
      }

      return new FrameDisplay(plugin, location, face, WIDTH, HEIGHT, frames, uuid);
    }
  }
}
