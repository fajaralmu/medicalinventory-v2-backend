package com.pkm.medicalinventory.service.resources;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImageRemovalService {
	@Value("${app.resources.apiRemoveEndpoint}")
	private String apiRemoveEndpoint; 
	RestTemplate restTemplate = new RestTemplate();
	
	public boolean removeImage(String imageName) {
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		String response = null;
		log.info("Post delete {} request to :{}", imageName, apiRemoveEndpoint);
		try {
			map.add("name", imageName);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiRemoveEndpoint, requestEntity, String.class);
			log.info("remove image code: {}", responseEntity.getStatusCode());
			response = responseEntity.getBody();
			return true;
		} catch (HttpStatusCodeException e) {
			e.printStackTrace();
			response = e.getResponseBodyAsString();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			response = e.getMessage();
			return false;
		} finally {
			log.info("remove image response: {}", response);
		}
		 
	}

}
