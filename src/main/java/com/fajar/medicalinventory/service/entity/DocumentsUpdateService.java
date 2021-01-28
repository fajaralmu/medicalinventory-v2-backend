package com.fajar.medicalinventory.service.entity;
//package com.fajar.medicalinventory.service.entity;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.fajar.medicalinventory.dto.WebResponse;
//import com.fajar.medicalinventory.entity.Documents;
//import com.fajar.medicalinventory.repository.DocumentRepository;
//import com.fajar.medicalinventory.util.CollectionUtil;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Slf4j
//public class DocumentsUpdateService extends BaseEntityUpdateService<Documents> {
//
//	@Autowired
//	private DocumentRepository documentsRepository; 
//
//	/**
//	 * add & update documents
//	 * 
//	 * @param documents
//	 * @param newRecord
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public WebResponse saveEntity(Documents baseEntity, boolean newRecord, HttpServletRequest httoHttpServletRequest) throws Exception {
//
//		Documents documents = (Documents) copyNewElement(baseEntity, newRecord);
//		Optional<Documents> dbDocuments = Optional.empty();
//		if (!newRecord) {
//			dbDocuments = documentsRepository.findById(documents.getId());
//			if (!dbDocuments.isPresent()) {
//			 
//				throw new Exception("Existing record not found");
//			}
//		} 
//		String documentData = documents.getDocuments();
//		if (documentData != null && !documentData.equals("")) {
//			log.info("documents document will be updated");
//			String documentUrl = null;
//			if (newRecord) {
//				documentUrl = writeNewDocuments(documents, documentData);
//			} else {
//				documentUrl = updateDocuments(documents, dbDocuments, documentData);
//			}
//			documents.setDocuments(documentUrl);
//		} else {
//			log.info("Documents document wont be updated");
//			if (!newRecord) {
//				documents.setDocuments(dbDocuments.get().getDocuments());
//			} 
//		}
//
//		Documents newDocuments = entityRepository.save(documents);
//		 
//
//		return WebResponse.builder().entity(newDocuments).build();
//	}
//
//	private String writeNewDocuments(Documents documents, String documentData) {
//		String[] rawDocumentList = documentData.split("~");
//		if (rawDocumentList == null || rawDocumentList.length == 0) {
//			return null;
//		}
//		List<String> documentUrls = new ArrayList<>();
//		for (int i = 0; i < rawDocumentList.length; i++) {
//			String base64Document = rawDocumentList[i];
//			if (base64Document == null || base64Document.equals(""))
//				continue;
//			try {
//				String documentName = fileService.writeDocument(documents.getClass().getSimpleName(), base64Document);
//				if (null != documentName) {
//					documentUrls.add(documentName);
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		if (documentUrls.size() == 0) {
//			return null;
//		}
//
//		String[] arrayOfString = documentUrls.toArray(new String[] {});
//		CollectionUtil.printArray(arrayOfString);
//
//		String documentUrlArray = String.join("~", arrayOfString);
//		documents.setDocuments(documentUrlArray);
//
//		return documentUrlArray;
//	}
//
//	private String updateDocuments(Documents documents, Optional<Documents> dbDocuments, String documentData) {
//		final String[] rawDocumentList = documentData.split("~");
//		if (rawDocumentList == null || rawDocumentList.length == 0 || dbDocuments.isPresent() == false) {
//			return null;
//		}
//		final boolean oldValueExist = dbDocuments.get().getDocuments() != null
//				&& dbDocuments.get().getDocuments().split("~").length > 0;
//		final String[] oldValueStringArr = oldValueExist ? documents.getDocuments().split("~") : new String[] {};
//		final List<String> documentUrls = new ArrayList<>();
//		//loop
//		log.info("rawDocumentList length: {}", rawDocumentList.length);
//		for (int i = 0; i < rawDocumentList.length; i++) {
//			final String rawDocument = rawDocumentList[i];
//			if (rawDocument == null || rawDocument.equals(""))
//				continue;
//			String documentName = null;
//			if (isBase64(rawDocument)) {
//				try {
//					documentName = fileService.writeDocument(documents.getClass().getSimpleName(), rawDocument);
//					log.info("saved base64 document {}", documentName);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			} else {
//
//				if (oldValueExist && inArray(rawDocument, oldValueStringArr)) {
//					documentName = rawDocument;
//				}
//			}
//
//			if (documentName != null) {
//				documentUrls.add(documentName);
//			}
//		}
//		if (documentUrls.size() == 0) {
//			return null;
//		}
//
//		String[] arrayOfString = documentUrls.toArray(new String[] {});
//		CollectionUtil.printArray(arrayOfString);
//
//		String documentUrlArray = String.join("~", arrayOfString);
//		documents.setDocuments(documentUrlArray);
//
//		return documentUrlArray;
//	}
//
//	private boolean inArray(String documentName, String[] array) {
//		for (int i = 0; i < array.length; i++) {
//			if (documentName.equals(array[i]))
//				return true;
//		}
//		
//		return false;
//	}
//
//	private boolean isBase64(String rawDocument) {
//
//		return rawDocument.startsWith("data:image");
//	}
//}
