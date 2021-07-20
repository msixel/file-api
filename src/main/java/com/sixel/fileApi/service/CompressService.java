package com.sixel.fileApi.service;

import java.io.IOException;

import com.sixel.fileApi.model.CompressedFile;

/**
 * @author msixe
 *
 */
public interface CompressService {
	
	/**
	 * @param compressedFile
	 * @throws IOException
	 */
	public void compressAsync(final CompressedFile compressedFile) throws IOException;

	/**
	 * @param compressedFile
	 * @return
	 * @throws IOException
	 */
	public CompressedFile compress(final CompressedFile compressedFile) throws IOException;

}
