/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.medical.application.device.web.common.util;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.osgi.framework.Bundle;

public class ImageUtil {

	public static BufferedImage mergeImages(BufferedImage image, BufferedImage overlay, int x, int y) {

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(image.getWidth(), x + overlay.getWidth());
		int h = Math.max(image.getHeight(), y + overlay.getHeight());
		BufferedImage combined = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.drawImage(overlay, x, y, null);

		return combined;
	}
	
	public static BufferedImage getBufferedImage(String imagePath, Bundle bundle) {
		URL imageUrl = bundle.getResource(imagePath);
		BufferedImage image = null;
        if (imageUrl != null)
			try {
				image = ImageIO.read(imageUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
        if (image == null) {
            throw new IllegalArgumentException("Specified resource does not exist: " + imagePath + ".");
        }
        
        return image;
	}

	public static BufferedImage resizeImage(BufferedImage originalImage,
			int resizeWidth, int resizeHeight) {
		
		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		
		BufferedImage resizedImage = new BufferedImage(resizeWidth, resizeHeight,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, resizeWidth, resizeHeight, null);
		g.dispose();

		return resizedImage;
	}

	public static BufferedImage resizeImageWithHint(
			BufferedImage originalImage, int resizeWidth, int resizeHeight) {

		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		
		BufferedImage resizedImage = new BufferedImage(resizeWidth, resizeHeight,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, resizeWidth, resizeHeight, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}
}
