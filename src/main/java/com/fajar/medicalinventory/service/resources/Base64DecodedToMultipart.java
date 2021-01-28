package com.fajar.medicalinventory.service.resources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

import com.fajar.medicalinventory.dto.AttachmentInfo;

/*
	*<p>
	* Trivial implementation of the {@link MultipartFile} interface to wrap a byte[] decoded
	* from a BASE64 encoded String
	*</p>
	*/
public class Base64DecodedToMultipart implements MultipartFile {
	private final byte[] imgContent;
	final AttachmentInfo attachmentInfo;

	public Base64DecodedToMultipart(AttachmentInfo attachmentInfo) throws  Exception {
		this.attachmentInfo = attachmentInfo;
		this.imgContent =   Base64.getDecoder().decode(attachmentInfo.getData().getBytes("UTF-8"));
	}

	@Override
	public String getName() {
		return "image";
	}

	@Override
	public String getOriginalFilename() {
		// TODO - implementation depends on your requirements
		return attachmentInfo.getName();
	}

	@Override
	public String getContentType() {
		// TODO - implementation depends on your requirements
		return "image/" + attachmentInfo.getExtension();
	}

	@Override
	public boolean isEmpty() {
		return imgContent == null || imgContent.length == 0;
	}

	@Override
	public long getSize() {
		return imgContent.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return imgContent;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(imgContent);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		new FileOutputStream(dest).write(imgContent);
	}
}
