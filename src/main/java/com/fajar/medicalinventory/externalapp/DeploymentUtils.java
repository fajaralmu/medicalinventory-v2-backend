package com.fajar.medicalinventory.externalapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
 @Slf4j
public class DeploymentUtils {

	static final String TARGET_DIRECTORY = "D:\\Development\\Fajar\\medicalinventory\\target\\";
	static final String WEBAPPS_DIRECTORY = "D:\\Development\\XamppPhp74\\tomcat\\webapps\\";
	static final String APP_NAME = "medicalinventory";
	 
	public static void main(String[] args) throws IOException {
		deleteDeployed();
		System.out.println("DELETE DEPLOYED");
		copyBuiltApp();
		log.info("DONE");
	}

	private static void copyBuiltApp() {
		File original = new File(TARGET_DIRECTORY + APP_NAME + ".war");
		File copied = new File(WEBAPPS_DIRECTORY + APP_NAME + ".war");
		try (InputStream in = new BufferedInputStream(new FileInputStream(original));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(copied))) {

			byte[] buffer = new byte[1024];
			int lengthRead;
			while ((lengthRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, lengthRead);
				out.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private static void deleteDeployed() throws IOException {
		File dir = new File(WEBAPPS_DIRECTORY);
		File[] files = dir.listFiles();
		for (File file : Arrays.asList(files)) {
			if (file.getName().equals(APP_NAME) || file.getName().equals(APP_NAME + ".war")) {
				if (file.isDirectory()) {
					FileUtils.deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
	}
}
