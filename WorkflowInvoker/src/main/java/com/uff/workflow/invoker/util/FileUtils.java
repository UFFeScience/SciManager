package com.uff.workflow.invoker.util;

import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class FileUtils {
	
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
	
	public static void createFileFromBytes(String filePath, String fileName, byte[] outputFileBytes) {
		try {
			File fileDir = new File(filePath);
			
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			
			FileOutputStream fileOuputStream = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileName); 
			fileOuputStream.write(outputFileBytes);
			fileOuputStream.close();
	    } 
		catch (Exception e) {
			log.error("Erro ao criar arquivo de path {} a partir de bytes.\n{}", filePath, Throwables.getStackTraceAsString(e));
			e.printStackTrace();
		}
	}
	
}