package com.uff.scimanager.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
	
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
	
	private static final String IMAGE_FORMAT = "png";
	private static final String TEMP_FILE_SUFFIX = ".png";
	private static final String TEMP_FILE_PREFIX = "image_profile";
	private static final String IMAGE_BASE_64_PREFIX = "data:image/png;base64,";
	private static final int IMG_WIDTH = 100;
	private static final int IMG_HEIGHT = 100;

	public static byte[] getBytesArray(File file) throws FileNotFoundException, IOException {
		InputStream input = new FileInputStream(file);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = input.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		
		input.close();
		buffer.flush();

		return buffer.toByteArray();
	}
	
	public static String encodeBytes(byte[] imageByteArray) {
		if (imageByteArray == null) {
			return null;
		}
		
        return IMAGE_BASE_64_PREFIX + Base64.encodeBase64String(imageByteArray);
    }
	
	public static byte[] processImageData(String imageDataString) throws IOException {
		log.info("Iniciando processo de decodificacao de bytes de arquivo de imagem de perfil");
		
		File tempImageFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, null);
		byte dearr[] = Base64.decodeBase64(imageDataString.substring(IMAGE_BASE_64_PREFIX.length()));
	    
		FileOutputStream fos = new FileOutputStream(tempImageFile); 
	    fos.write(dearr); 
	    fos.close();
	    
	    return resizeImage(tempImageFile);
	}

	public static byte[] resizeImage(File imageFile) throws IOException {
		log.info("Realizando resize de imagem de perfil");
		
		BufferedImage originalImage = ImageIO.read(imageFile);
		
		if (originalImage.getWidth() < IMG_WIDTH && originalImage.getHeight() < IMG_HEIGHT) {
			byte[] bytesArray = new byte[(int) imageFile.length()];

			FileInputStream fileInputStream = new FileInputStream(imageFile);
	        fileInputStream.read(bytesArray);
	        fileInputStream.close();
			
			return bytesArray;
		}
		
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
				
		BufferedImage resizedImageHintPng = resizeImageWithHint(originalImage, type);
		ImageIO.write(resizedImageHintPng, IMAGE_FORMAT, imageFile);
		
		byte[] bytesArray = new byte[(int) imageFile.length()];

		FileInputStream fileInputStream = new FileInputStream(imageFile);
        fileInputStream.read(bytesArray);
        fileInputStream.close();
		
		return bytesArray;
	}
	
	private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D graphics = resizedImage.createGraphics();
		
		graphics.setComposite(AlphaComposite.Src);

		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		graphics.drawImage(originalImage.getScaledInstance(IMG_WIDTH, IMG_WIDTH, Image.SCALE_SMOOTH), 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		graphics.dispose();

		return resizedImage;
    }
	
}