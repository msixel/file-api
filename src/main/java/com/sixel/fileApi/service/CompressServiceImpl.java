package com.sixel.fileApi.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sixel.fileApi.model.CompressedFile;
import com.sixel.fileApi.model.StoredFile;

/**
 * @author msixe
 *
 */
@Service
public class CompressServiceImpl implements CompressService {

	/**
	 * {@inheritDoc}
	 */
	@Async
	@Override
	public void compressAsync(final CompressedFile compressedFile) throws IOException {
		compress(compressedFile);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompressedFile compress(final CompressedFile compressedFile) throws IOException {
        try (final FileOutputStream fos = new FileOutputStream(compressedFile.getPath().toFile()); 
        		final ZipOutputStream zipOut = new ZipOutputStream(fos)) {
        	
        	for (final StoredFile storedFile: compressedFile.getStoredFiles()) {
        		
        		try (final FileInputStream fis = new FileInputStream(storedFile.getPath().toFile())) {
                    final ZipEntry zipEntry = new ZipEntry(storedFile.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[8192];
                    int length;
                    while((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }        			
        		}
            }
        }
        
		compressedFile.setSize(compressedFile.getPath().toFile().length());
		return compressedFile;
	}

}
