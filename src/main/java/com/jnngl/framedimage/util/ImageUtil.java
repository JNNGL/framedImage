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

package com.jnngl.framedimage.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageUtil {

  public static List<BufferedImage> readFrames(String urlString) throws IOException {
    URL url = new URL(urlString);
    String type = URLConnection.guessContentTypeFromName(urlString);

    if (type == null) {
      return Collections.singletonList(ImageIO.read(url));
    }

    try (ImageInputStream stream = ImageIO.createImageInputStream(url.openStream())) {
      if (type.equals("image/gif")) {
        return GifReader.read(stream);
      } else {
        ImageReader imageReader = ImageIO.getImageReadersByMIMEType(type).next();
        imageReader.setInput(stream);

        int frameCount = imageReader.getNumImages(true);

        List<BufferedImage> frames = new ArrayList<>(frameCount);
        for (int i = 0; i < frameCount; i++) {
          frames.add(imageReader.read(i));
        }

        return frames;
      }
    }
  }
}
