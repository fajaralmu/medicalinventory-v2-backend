package com.fajar.medicalinventory.externalapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
 @Slf4j
public class DeploymentUtils {


	static final String DIR = "D:\\Development\\Fajar\\medicalinventory";
	static final String TARGET_DIRECTORY = DIR+"\\target\\";
	static final String WEBAPPS_DIRECTORY = "E:\\123Program\\apache-tomcat-9.0.37\\webapps\\";
//	static final String WEBAPPS_DIRECTORY = "D:\\Development\\XamppPhp74\\tomcat\\webapps\\";
	static final String APP_NAME = "medicalinventory";
	 
	public static void main(String[] args) throws Exception {
		runCommand();
		deleteDeployed();
		copyBuiltApp();
		log.info("DONE");
	}

	public static void runCommand() throws Exception {
		System.out.println("===================================");
        ProcessBuilder builder = new ProcessBuilder(
            "cmd.exe", "/c", "cd /d \""+DIR+"\" && mvn -o clean package");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            System.out.println(line);
        }
        System.out.println("===================================");
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
			System.out.println("COPIED new War File");
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
					System.out.println("DELETE DEPLOYED Directory");
				} else {
					file.delete();
					System.out.println("DELETE DEPLOYED War File");
				}
			}
		}
	}
}
