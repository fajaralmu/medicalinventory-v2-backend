package com.pkm.medicalinventory.service.resources;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FtpResourceService {
	
	@Value("${app.resources.ftpServer}")
	private String ftpServer;
	@Value("${app.resources.ftpPort}")
	private int ftpPort;
	@Value("${app.resources.ftpUser}")
	private String ftpUser;
	@Value("${app.resources.ftpPassword}")
	private String ftpPassword;
	@Value("${app.resources.ftpBaseDirectory}")
	private String ftpBaseDirectory;

	public BufferedImage getImage(String name) throws Exception {
		 
		ByteArrayOutputStream output = getImageAsOutputStream(name);
		return ImageIO.read(toInputStream(output));
	}
	public ByteArrayOutputStream getImageAsOutputStream(String name) throws Exception {
		FtpClient ftpClient = ftpClientInstance();
		ftpClient.open();
		ByteArrayOutputStream output = ftpClient.getUploadedImage(name);
		ftpClient.completePendingCommand();
		ftpClient.close();
		return output;
	}
	public ByteArrayInputStream getImageAsInputStream(String name) throws Exception {
		
		ByteArrayOutputStream output = getImageAsOutputStream(name);
		return new ByteArrayInputStream(output.toByteArray());
	}

	private ByteArrayInputStream toInputStream(ByteArrayOutputStream output) {
		byte[] data = output.toByteArray();
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		return input;
	}
	
	
	
	public void storeFtp(String imageString, String imageFileName)  {
		log.info("store ftp: {}", imageFileName);
		try {
			FtpClient ftpClient = ftpClientInstance();
			ftpClient.open();
			ftpClient.storeBase64Image(imageString, imageFileName);
			ftpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private FtpClient ftpClientInstance() {
		FtpClient ftpClient = new FtpClient(ftpServer, ftpPort, ftpUser, ftpPassword);
		ftpClient.setBaseDirectory(ftpBaseDirectory);
		return ftpClient;
	}
}
