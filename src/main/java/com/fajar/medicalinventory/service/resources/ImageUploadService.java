package com.fajar.medicalinventory.service.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.setting.MultipleImageModel;
import com.fajar.medicalinventory.entity.setting.SingleImageModel;
import com.fajar.medicalinventory.repository.EntityRepository;
import com.fajar.medicalinventory.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImageUploadService {
	@Autowired
	private FileService fileService;
	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private ImageRemovalService imageRemovalService;

	/**
	 * upload single image
	 * 
	 * @param singleImageModel
	 * @return
	 */
	public String uploadImage(SingleImageModel singleImageModel) {

		String image = singleImageModel.getImage();
		if (image != null && image.startsWith("data:image")) {

			if (null != singleImageModel.getId()) {
				removeOldImage(singleImageModel);
			}
			try {
				String savedFileName = fileService.writeImage(singleImageModel.getClass().getSimpleName(), image);
				singleImageModel.setImage(savedFileName);
				return savedFileName;
			} catch (IOException e) {
				e.printStackTrace();
				singleImageModel.setImage(null);
			}

		}
		return image;
	}

	private void removeOldImage(SingleImageModel singleImageModel) {
		BaseEntity existingRecord = entityRepository.findById(((BaseEntity)singleImageModel).getClass(), singleImageModel.getId());
		if (null == existingRecord) {
			return;
		}
		SingleImageModel existingImageModel = (SingleImageModel) existingRecord;
		if (null != existingImageModel.getImage()) {
			imageRemovalService.removeImage(existingImageModel.getImage());
		}
	}

	/**
	 * upload multiple images
	 * 
	 * @param multipleImageModel
	 * @param httpServletRequest
	 * @return
	 */
	public String writeNewImages(MultipleImageModel multipleImageModel, HttpServletRequest httpServletRequest) {
		String[] rawImageList = multipleImageModel.getImageNames();
		if (rawImageList == null || rawImageList.length == 0) {
			return null;
		}
		List<String> imageUrls = new ArrayList<>();
		for (int i = 0; i < rawImageList.length; i++) {
			String base64Image = rawImageList[i];
			if (base64Image == null || base64Image.equals(""))
				continue;
			try {
				String imageName = fileService.writeImage(multipleImageModel.getClass().getSimpleName(), base64Image,
						httpServletRequest);
				if (null != imageName) {
					imageUrls.add(imageName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (imageUrls.size() == 0) {
			return null;
		}

		String[] arrayOfString = imageUrls.toArray(new String[] {});
		CollectionUtil.printArray(arrayOfString);

		String imageUrlArray = String.join("~", arrayOfString);
		multipleImageModel.setImageNames(arrayOfString);

		return imageUrlArray;
	}

	/**
	 * update multiple images
	 * 
	 * @param multipleImageModel
	 * @param exixstingMultipleImageModel
	 * @param httpServletRequest
	 * @return
	 */
	public String updateImages(MultipleImageModel multipleImageModel, MultipleImageModel exixstingMultipleImageModel,
			HttpServletRequest httpServletRequest) {
		final String[] rawImageList = multipleImageModel.getImageNames();
		if (rawImageList == null || rawImageList.length == 0 || exixstingMultipleImageModel == null) {
			return null;
		}
		final boolean oldValueExist = exixstingMultipleImageModel.getImageNames().length > 0;
		final String[] oldValueStringArr = oldValueExist ? multipleImageModel.getImageNames() : new String[] {};
		final List<String> imageUrls = new ArrayList<>();
		// loop
		log.info("rawImageList length: {}", rawImageList.length);
		for (int i = 0; i < rawImageList.length; i++) {
			final String rawImage = rawImageList[i];
			if (rawImage == null || rawImage.equals(""))
				continue;
			String imageName = null;
			if (isBase64Image(rawImage)) {
				try {
					imageName = fileService.writeImage(multipleImageModel.getClass().getSimpleName(), rawImage);
					log.info("saved base64 image {}", imageName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {

				if (oldValueExist && inArray(rawImage, oldValueStringArr)) {
					imageName = rawImage;
				}
			}

			if (imageName != null) {
				imageUrls.add(imageName);
			}
		}
		if (imageUrls.size() == 0) {
			return null;
		}

		String[] arrayOfString = imageUrls.toArray(new String[] {});
		CollectionUtil.printArray(arrayOfString);

		String imageUrlArray = String.join("~", arrayOfString);
		multipleImageModel.setImageNames(arrayOfString);

		return imageUrlArray;
	}

	private boolean inArray(String imageName, String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (imageName.equals(array[i]))
				return true;
		}

		return false;
	}

	private boolean isBase64Image(String rawImage) {

		return rawImage.startsWith("data:image");
	}
}
