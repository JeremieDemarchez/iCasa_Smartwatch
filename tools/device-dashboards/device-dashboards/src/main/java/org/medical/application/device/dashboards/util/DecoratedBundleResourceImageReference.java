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
package org.medical.application.device.dashboards.util;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import nextapp.echo.app.Extent;
import nextapp.echo.app.ResourceImageReference;

import org.osgi.framework.Bundle;


/**
 * Represents an image decorated with two images.
 *
 * @author Thomas Leveque
 *
 */
public class DecoratedBundleResourceImageReference extends ResourceImageReference {

	private static final int IMG_HEIGHT = 30;

	private static final int IMG_WIDTH = 30;
	
	private static final int RIGHT_DECO_IMG_HEIGHT = 16;

	private static final int RIGHT_DECO_IMG_WIDTH = 16;
	
	private static final int CENTER_DECO_IMG_HEIGHT = 25;

	private static final int CENTER_DECO_IMG_WIDTH = 25;

	/**
    * 
    */
   private static final long serialVersionUID = 3152621476928787288L;

   /**
    * Bundle that has the image as resource
    */
   private Bundle _bundle;

   private String _rightDecoratorImg;

   private String _centeredDecoratorImg;

   private Bundle _decoratorBundle;
   
   /**
    * 
    * All images must be contained in the specified bundle.
    * 
    * @param image
    * @param rightDecoratorImg
    * @param centeredDecoratorImg
    * @param bundle
 * @param decoratorBundle 
    */
   public DecoratedBundleResourceImageReference(String image, String rightDecoratorImg, String centeredDecoratorImg, Bundle bundle, Bundle decoratorBundle) {
   		super(image, new Extent(IMG_WIDTH, Extent.PX), new Extent(IMG_HEIGHT, Extent.PX));
   		_bundle = bundle;
   		_decoratorBundle = decoratorBundle;
   		_rightDecoratorImg = rightDecoratorImg;
   		_centeredDecoratorImg = centeredDecoratorImg;
   }
   
   /**
    * 
    * All images must be contained in the specified bundle.
    * 
    * @param image
    * @param rightDecoratorImg
    * @param bundle
    */
   public DecoratedBundleResourceImageReference(String image, String rightDecoratorImg, Bundle bundle, Bundle decoratorBundle) {
	   this(image, rightDecoratorImg, null, bundle, decoratorBundle);
   }
   
   /**
    * 
    * All images must be contained in the specified bundle.
    * 
    * @param image
    * @param bundle
    */
   public DecoratedBundleResourceImageReference(String image,Bundle bundle) {
   		this(image, null, null, bundle, null);
   }
   
   @Override
   public void render(OutputStream out) throws IOException {
    
      try {
    	  BufferedImage originalImage = ImageUtil.getBufferedImage(getResource(), _bundle);
    	  BufferedImage rightDecoratorImage = null;
          if (_rightDecoratorImg != null) {
        	  rightDecoratorImage = ImageUtil.getBufferedImage(_rightDecoratorImg, _decoratorBundle);
          }
          BufferedImage centeredDecoratorImage = null;
          if (_centeredDecoratorImg != null) {
        	  centeredDecoratorImage = ImageUtil.getBufferedImage(_centeredDecoratorImg, _decoratorBundle);
          }
          
          // resize image
          BufferedImage resultImage = ImageUtil.resizeImage(originalImage, IMG_WIDTH, IMG_HEIGHT);
          
          // add right decorator
          if (rightDecoratorImage != null) {
        	  BufferedImage resizRightDecoImage = ImageUtil.resizeImage(rightDecoratorImage, RIGHT_DECO_IMG_WIDTH, RIGHT_DECO_IMG_HEIGHT);
        	  final int posX = IMG_WIDTH - RIGHT_DECO_IMG_WIDTH;
			  final int posY = IMG_HEIGHT - RIGHT_DECO_IMG_HEIGHT;
			  resultImage = ImageUtil.mergeImages(resultImage, resizRightDecoImage, 
        			  posX, posY);
          }
          
          // add center decorator
          if (centeredDecoratorImage != null) {
        	  BufferedImage resizCenterDecoImage = ImageUtil.resizeImage(centeredDecoratorImage, CENTER_DECO_IMG_WIDTH, CENTER_DECO_IMG_HEIGHT);
        	  final int posX = (IMG_WIDTH - CENTER_DECO_IMG_WIDTH) / 2;
			  final int posY = (IMG_HEIGHT - CENTER_DECO_IMG_HEIGHT) / 2;
			  resultImage = ImageUtil.mergeImages(resultImage, resizCenterDecoImage, 
        			  posX, posY);
          }
          
          ImageIO.write(resultImage, "PNG", out);
          
      } finally {
          
          // do nothing
      }
   }
   
}
