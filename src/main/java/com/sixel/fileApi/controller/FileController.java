package com.sixel.fileApi.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sixel.fileApi.model.CompressedFile;
import com.sixel.fileApi.model.StoredFile;
import com.sixel.fileApi.service.FileService;

/**
 * @author msixe
 *
 */
@RestController
@RequestMapping("api/file/v1")
public class FileController {
	
	@Autowired
	private FileService fileService;
	
	private HttpHeaders baseHeaders;
	
	@PostConstruct
	private void postConstruct() {
		baseHeaders = new HttpHeaders();
		baseHeaders.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
		baseHeaders.add(HttpHeaders.PRAGMA, "no-cache");
		baseHeaders.add(HttpHeaders.EXPIRES, "0");
	}
	
	/**
	 * @return
	 */
	private HttpHeaders buildHttpHeaders() {
		return new HttpHeaders(baseHeaders);
	}
	
	/**
	 * @param multipartFile
	 * @return
	 */
	@PostMapping(path = "/upload", consumes = "multipart/form-data", produces = "application/json")
	public ResponseEntity<StoredFile> upload(
			final @RequestParam(name = "file", required = true) MultipartFile multipartFile) {

		if (multipartFile.isEmpty()) {
			return ResponseEntity.badRequest().body(null);
		}

		try {
			final StoredFile storedFile = fileService.store(multipartFile);
			return ResponseEntity.ok().body(storedFile);

		} catch (Throwable t) {
			t.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
	}
	
	/**
	 * @param storedFiles
	 * @return
	 */
	@PostMapping(path = "/compress", consumes = "application/json",  produces = "application/json")
	public ResponseEntity<CompressedFile> compress(
			final @RequestBody(required = true) Set<UUID> storedFileUUIDs,
			final @RequestParam(name="async", defaultValue = "false", required = false) Boolean async) {
		
		final CompressedFile compressedFile;
		
		if (storedFileUUIDs.isEmpty()) {
			return ResponseEntity.badRequest().body(null);
		}

		try {
			compressedFile = fileService.compress(storedFileUUIDs, async);
			return ResponseEntity.ok().body(compressedFile);

		} catch (Throwable t) {
			t.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
	}
	
	/**
	 * @param uuid
	 * @throws IOException 
	 */
	@GetMapping(path = "/download/{uuid}", produces = "application/octet-stream")
	public ResponseEntity<Resource> download(
			final @PathVariable(required = true) UUID uuid) {
		
		final StoredFile storedFile;
		final InputStreamResource stream;
		final HttpHeaders httpHeaders = buildHttpHeaders();
		
		try {
			storedFile = fileService.findByUUID(uuid);
			if (storedFile == null || NumberUtils.LONG_ZERO.equals(storedFile.getSize())) {
				httpHeaders.add(HttpHeaders.CONTENT_LENGTH, NumberUtils.LONG_ZERO.toString());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders).body(null);
			}
			
			httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + storedFile.getName());
			httpHeaders.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(storedFile.getSize()));
			httpHeaders.add(HttpHeaders.CONTENT_TYPE, storedFile.getContentType());

			stream = new InputStreamResource(new FileInputStream(storedFile.getPath().toFile()));
			return ResponseEntity.ok().headers(httpHeaders).body(stream);
			
		} catch (Throwable t) {
			t.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}

	}

}