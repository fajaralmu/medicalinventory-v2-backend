package com.pkm.medicalinventory.management;

import org.springframework.ui.Model;

import com.pkm.medicalinventory.dto.WebResponse;

public interface MasterDataManagementPageService {

	WebResponse getManagementPages();

	Model setModel(Model concurrentModel, String key);

}
