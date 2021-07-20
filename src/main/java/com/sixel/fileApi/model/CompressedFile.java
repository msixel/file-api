package com.sixel.fileApi.model;

import java.util.Set;

/**
 * @author msixe
 *
 */
public class CompressedFile extends StoredFile {
	
	/**
	 * 
	 */
	public static final String CONTENT_TYPE_ZIP = "application/zip";
	
	private Set<StoredFile> storedFiles;

	public Set<StoredFile> getStoredFiles() {
		return storedFiles;
	}

	public void setStoredFiles(final Set<StoredFile> storedFiles) {
		this.storedFiles = storedFiles;
	}
	
}
