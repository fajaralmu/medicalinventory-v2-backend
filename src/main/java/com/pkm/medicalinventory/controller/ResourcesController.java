package com.pkm.medicalinventory.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pkm.medicalinventory.service.resources.FtpResourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("assets")
public class ResourcesController extends BaseController{
	
	@Autowired
	private FtpResourceService resourceService;

	@RequestMapping(value = { "/images/{name:.+}"}) 
	public void images(@PathVariable(name="name") String name, HttpServletRequest request, HttpServletResponse response) throws Exception  { 
		 
		ByteArrayInputStream image = resourceService.getImageAsInputStream(name);
		 
		String[] nameSplitted = name.split("\\.");
		String type = nameSplitted[nameSplitted.length-1];
		response.setHeader("Content-Type", "image/"+type);
		response.setHeader("Content-Length", String.valueOf(image.available()));
		response.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");

		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
		    input = new BufferedInputStream(image);
		    output = new BufferedOutputStream(response.getOutputStream());
		    byte[] buffer = new byte[8192];
		    int length = 0;

			while ((length = input.read(buffer)) > 0){
				output.write(buffer, 0, length);
			}
		} finally {
		    if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
		    if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
		}
		 
	}
}
