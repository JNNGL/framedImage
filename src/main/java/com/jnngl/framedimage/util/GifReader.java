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

package com.jnngl.framedimage.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GifReader {

  public static List<BufferedImage> read(ImageInputStream inputStream) throws IOException {
    List<BufferedImage> frames = new ArrayList<>();
    List<String> disposals = new ArrayList<>();

    ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
    reader.setInput(inputStream);

    int lastX = 0;
    int lastY = 0;
    int width = -1;
    int height = -1;

    IIOMetadata metadata = reader.getStreamMetadata();
    Color background = null;

    if (metadata != null) {
      IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

      NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
      NodeList globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

      if (globalScreenDescriptor.getLength() > 0) {
        IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreenDescriptor.item(0);

        if (screenDescriptor != null) {
          width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
          height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
        }
      }

      if (globalColorTable.getLength() > 0) {
        IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

        if (colorTable != null) {
          String backgroundIndex = colorTable.getAttribute("backgroundColorIndex");

          IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
          while (colorEntry != null) {
            if (colorEntry.getAttribute("index").equals(backgroundIndex)) {
              int red = Integer.parseInt(colorEntry.getAttribute("red"));
              int green = Integer.parseInt(colorEntry.getAttribute("green"));
              int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

              background = new Color(red, green, blue);
              break;
            }

            colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
          }
        }
      }
    }

    BufferedImage master = null;
    boolean hasBackground = false;

    for (int frameIndex = 0; frameIndex < reader.getNumImages(true); frameIndex++) {
      BufferedImage image = reader.read(frameIndex);

      if (width == -1 || height == -1) {
        width = image.getWidth();
        height = image.getHeight();
      }

      IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
      IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
      NodeList children = root.getChildNodes();

      String disposal = gce.getAttribute("disposalMethod");

      if (master == null) {
        master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = master.createGraphics();
        graphics.setColor(background);
        graphics.fillRect(0, 0, master.getWidth(), master.getHeight());
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        hasBackground = image.getWidth() == width && image.getHeight() == height;
      } else {
        int x = 0;
        int y = 0;

        for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
          Node nodeItem = children.item(nodeIndex);

          if (nodeItem.getNodeName().equals("ImageDescriptor")) {
            NamedNodeMap map = nodeItem.getAttributes();

            x = Integer.parseInt(map.getNamedItem("imageLeftPosition").getNodeValue());
            y = Integer.parseInt(map.getNamedItem("imageTopPosition").getNodeValue());
          }
        }

        if (disposal.equals("restoreToPrevious")) {
          BufferedImage from = null;
          for (int i = frameIndex - 1; i >= 0; i--) {
            if (!disposals.get(i).equals("restoreToPrevious")) {
              from = frames.get(i);
              break;
            }
          }

          if (from != null) {
            ColorModel model = from.getColorModel();
            boolean alpha = from.isAlphaPremultiplied();
            WritableRaster raster = from.copyData(null);
            master = new BufferedImage(model, raster, alpha, null);
          }
        } else if (disposal.equals("restoreToBackgroundColor") && background != null) {
          if (!hasBackground || frameIndex > 1) {
            Graphics2D graphics = master.createGraphics();
            BufferedImage previous = frames.get(frameIndex - 1);
            graphics.fillRect(lastX, lastY, previous.getWidth(), previous.getHeight());
            graphics.dispose();
          }
        }

        Graphics2D graphics = master.createGraphics();
        graphics.drawImage(image, x, y, null);
        graphics.dispose();

        lastX = x;
        lastY = y;
      }

      ColorModel model = master.getColorModel();
      boolean alpha = model.isAlphaPremultiplied();
      WritableRaster raster = master.copyData(null);
      BufferedImage copy = new BufferedImage(model, raster, alpha, null);

      frames.add(copy);
      disposals.add(disposal);

      master.flush();
    }

    reader.dispose();

    return frames;
  }
}
