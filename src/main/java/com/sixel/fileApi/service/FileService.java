package com.sixel.fileApi.service;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.sixel.fileApi.model.CompressedFile;
import com.sixel.fileApi.model.StoredFile;

/**
 * @author msixe
 *
 */
public interface FileService {
	
	/**
	 * @param multipartFile
	 * @return
	 * @throws IOException
	 */
	public StoredFile store (final MultipartFile multipartFile) throws IOException;
	
	/**
	 * @param storedFileUUIDs
	 * @return
	 * @throws IOException 
	 */
	public CompressedFile compress(final Set<UUID> storedFileUUIDs, final Boolean async) throws IOException;
	
	/**
	 * @param uuid
	 * @return
	 * @throws IOException 
	 */
	public StoredFile findByUUID(final UUID uuid) throws IOException;

}
