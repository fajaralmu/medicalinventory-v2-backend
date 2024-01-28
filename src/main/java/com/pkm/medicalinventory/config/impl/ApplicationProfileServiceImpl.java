package com.pkm.medicalinventory.config.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.config.ApplicationProfileService;
import com.pkm.medicalinventory.constants.FontAwesomeIcon;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.model.ApplicationProfileModel;
import com.pkm.medicalinventory.entity.ApplicationProfile;
import com.pkm.medicalinventory.repository.main.AppProfileRepository;
import com.pkm.medicalinventory.repository.main.EntityRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApplicationProfileServiceImpl implements ApplicationProfileService {
	@Autowired
	private AppProfileRepository appProfileRepository;
	@Autowired
	private EntityRepository entityRepository;
	@Value("${app.resources.assets.path}")
	private String assetsPath;

	private ApplicationProfile applicationProfile;

	@PostConstruct
	public void init() {
		try {
			checkApplicationProfile();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public ApplicationProfile getApplicationProfile() {
		applicationProfile.setAssetsPath(assetsPath);
		return applicationProfile;
	}

	private void checkApplicationProfile() {
		log.info("Checking default app profile");
		
		ApplicationProfile profile = appProfileRepository.findByAppCode("MY_APP");
		if (null == profile) {
			profile = saveDefaultProfile();
		}
		this.applicationProfile = profile;
	}

	private ApplicationProfile saveDefaultProfile() {
		ApplicationProfile profile = new ApplicationProfile();
		profile.setName("Medical Inventory");
		profile.setAbout("");
		profile.setWebsite("http://localhost:3000");
		profile.setIconUrl("DefaultIcon.BMP");
		profile.setColor("#1e1e1e");
		profile.setFontColor("#f5f5f5");
		profile.setBackgroundUrl("Profile_02adb5ae-40b3-4b79-bc5e-f93c0ea8644f.png");
		profile.setPageIcon("ICO_8601834213.ico");
		profile.setAppCode("MY_APP");
		profile.setContact("somabangsa@gmail.com");
		profile.setFooterIconClass(FontAwesomeIcon.COFFEE);
		profile.setAbout("About My Retail");
		return appProfileRepository.save(profile);
	}

	public ApplicationProfileModel updateApplicationProfile(WebRequest webRequest) {
		log.info("Update application profile");

		final ApplicationProfile actualAppProfile = getApplicationProfile();
		final ApplicationProfile applicationProfile = webRequest.getProfile().toEntity();
		updateApplicationProfileData(actualAppProfile, applicationProfile);

		return actualAppProfile.toModel();
	}

	private void updateApplicationProfileData(
		ApplicationProfile existing,
		ApplicationProfile requested) {

		if (notEmpty(requested.getName())) {
			existing.setName(requested.getName());
		}
		if (notEmpty(requested.getWelcomingMessage())) {
			existing.setWelcomingMessage(requested.getWelcomingMessage());
		}
		if (notEmpty(requested.getShortDescription())) {
			existing.setShortDescription(requested.getShortDescription());
		}
		if (notEmpty(requested.getAbout())) {
			existing.setAbout(requested.getAbout());
		}
		if (notEmpty(requested.getColor())) {
			existing.setColor(requested.getColor());
		}
		if (notEmpty(requested.getContact())) {
			existing.setContact(requested.getContact());
		}
		if (notEmpty(requested.getFontColor())) {
			existing.setFontColor(requested.getFontColor());
		}
//		if (notEmpty(appProfile.getBackgroundUrl()) && appProfile.getBackgroundUrl().startsWith("data:image")) {
//			try {
//				String backgroundUrl = fileService.writeImage(ApplicationProfile.class.getSimpleName(),
//						appProfile.getBackgroundUrl(), httpServletRequest);
//				actualAppProfile.setBackgroundUrl(backgroundUrl);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		}
//		if (notEmpty(appProfile.getPageIcon()) && appProfile.getPageIcon().startsWith("data:image")) {
//			try {
//				String iconUrl = fileService.writeIcon(ApplicationProfile.class.getSimpleName(),
//						appProfile.getPageIcon(), httpServletRequest);
//				actualAppProfile.setPageIcon(iconUrl);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		if (notEmpty(appProfile.getIconUrl()) && appProfile.getIconUrl().startsWith("data:image")) {
//			try {
//				String iconUrl = fileService.writeImage(ApplicationProfile.class.getSimpleName(),
//						appProfile.getIconUrl(), httpServletRequest);
//				actualAppProfile.setIconUrl(iconUrl);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		if (notEmpty(appProfile.getName())) {
//			actualAppProfile.setName(appProfile.getName());
//		}

		entityRepository.save(existing);
	}

	private boolean notEmpty(String val) {
		return null != val && val.isEmpty() == false;
	}
}
