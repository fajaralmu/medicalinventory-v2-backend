package com.pkm.medicalinventory.config;

import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.model.ApplicationProfileModel;
import com.pkm.medicalinventory.entity.ApplicationProfile;

public interface ApplicationProfileService {
	ApplicationProfile getApplicationProfile();
	ApplicationProfileModel updateApplicationProfile(WebRequest webRequest);
}
