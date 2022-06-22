package com.pkm.medicalinventory.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pkm.medicalinventory.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8953767869805765435L;
	private static final double MAX_SIZE = 9000.d; //9KB
	private String name;
	private String extension;
	private String data;
	private String url;
	private int total;
	private int order;
	
	public static List<AttachmentInfo> extractAttachmentInfos(String data, String name, String extension) {
		List<AttachmentInfo> result = new ArrayList<AttachmentInfo>();
		int fileSize = StringUtil.base64StringFileSize(data);
		if (fileSize <= MAX_SIZE) {
			AttachmentInfo attachmentInfo = new AttachmentInfo();
			attachmentInfo.setData(data);
			attachmentInfo.setTotal(1);
			attachmentInfo.setOrder(1);
			attachmentInfo.setName(name);
			result.add(attachmentInfo);
			return result;
		}
		double division =  Math.ceil(fileSize/MAX_SIZE);
		double partialSize =  Math.ceil(data.length()/division);
		System.out.println("fileSize: "+fileSize);
		System.out.println("Max fileSize: "+MAX_SIZE);
		System.out.println("division: "+division);
		System.out.println("partialSize: "+partialSize);
		String[] dividedString = StringUtil.divideStringInto(data, partialSize);
		for (int i = 0; i < dividedString.length; i++) {
			AttachmentInfo attachmentInfo = new AttachmentInfo();
			attachmentInfo.setData(dividedString[i]);
			attachmentInfo.setTotal((int)division);
			attachmentInfo.setOrder(i + 1);
			attachmentInfo.setName(name);
			result.add(attachmentInfo);
		}
		
		return result ;
	}

}
