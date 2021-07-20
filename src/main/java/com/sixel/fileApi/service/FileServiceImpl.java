package com.sixel.fileApi.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sixel.fileApi.model.CompressedFile;
import com.sixel.fileApi.model.StoredFile;

/**
 * @author msixe
 *
 */
@Service
public class FileServiceImpl implements FileService {

	private static final String ORIGINALFILE_SUBDIRECTORY = "bin";
	
	@Autowired
	private CompressService compressService;
	
	@Value("${compressed.filename}")
	private String compressedFilename;
	
	@Value("${content-type.filename}")
	private String contentTypeFilename;
	
	@Value("${upload.temp.path}")
	private Path uploadFolderPath;
	
	/**
	 * @throws IOException
	 */
	@PostConstruct 
	private void postConstruct() throws IOException {
		if (Files.notExists(uploadFolderPath)) {
			Files.createDirectories(uploadFolderPath);
		}
		if (!(Files.isDirectory(uploadFolderPath) && Files.isWritable(uploadFolderPath))) {
			throw new IllegalStateException("check temp path");
		}
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	private Pair<UUID, Path> allocateUUID() throws IOException {
		UUID uuid;
		Path uuidFolderPath;
		
		do {
			uuid = UUID.randomUUID();
			uuidFolderPath = uploadFolderPath.resolve(uuid.toString());
		} while (Files.exists(uuidFolderPath));
		Files.createDirectory(uuidFolderPath);
		return new ImmutablePair<UUID, Path>(uuid, uuidFolderPath);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public StoredFile store(final MultipartFile multipartFile) throws IOException {
		final StoredFile storedFile = new StoredFile();
		final Pair<UUID, Path> pair;
		final Path contentTypeFilePath;
		final Path originalStoreFolderPath;
		
		storedFile.setName(multipartFile.getOriginalFilename());
		storedFile.setSize(multipartFile.getSize());
		storedFile.setContentType(multipartFile.getContentType());
		pair = allocateUUID();
		storedFile.setUuid(pair.getKey());
		
		contentTypeFilePath = pair.getValue().resolve(contentTypeFilename);
		Files.write(contentTypeFilePath, multipartFile.getContentType().toString().getBytes());
		
		originalStoreFolderPath = pair.getValue().resolve(ORIGINALFILE_SUBDIRECTORY);
		Files.createDirectory(originalStoreFolderPath);
		storedFile.setPath(originalStoreFolderPath.resolve(storedFile.getName()));	
		Files.write(storedFile.getPath(), multipartFile.getBytes());
		
		return storedFile;
	}

	/**
	 * {@inheritDoc}
	 * @throws IOException 
	 */
	@Override
	public CompressedFile compress(final Set<UUID> storedFileUUIDs, final Boolean async) throws IOException {
		final CompressedFile compressedFile = new CompressedFile();
		final Pair<UUID, Path> pair;
		final Path contentTypeFile;

		compressedFile.setName(compressedFilename);
		pair = allocateUUID();
		compressedFile.setUuid(pair.getKey());
		compressedFile.setPath(pair.getValue().resolve(compressedFile.getName()));
		compressedFile.setContentType(compressedFile.CONTENT_TYPE_ZIP);
		
		contentTypeFile = pair.getValue().resolve(contentTypeFilename);
		Files.write(contentTypeFile, compressedFile.getContentType().getBytes());
		
		compressedFile.setStoredFiles(
				storedFileUUIDs.stream().map(uuid -> {
					try {
						return findByUUID(uuid);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}).collect(Collectors.toSet()));
		
		if (Boolean.TRUE.equals(async)) {
			compressService.compressAsync(compressedFile);
			return compressedFile;
		} else {
			return compressService.compress(compressedFile);
		}
				
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public StoredFile findByUUID(final UUID uuid) throws IOException {
		final StoredFile storedFile;
		final Path uuidFolderPath;
		final Path originalStoreFolderPath;
		final Path filePath;
		final Path contentTypeFile;
		
		uuidFolderPath = uploadFolderPath.resolve(uuid.toString());
		if (Files.notExists(uuidFolderPath) || 
				!Files.isDirectory(uuidFolderPath) ||
				!Files.isReadable(uuidFolderPath)) {
			return null;
		}
		storedFile = new StoredFile();
		storedFile.setUuid(uuid);
		
		originalStoreFolderPath = uuidFolderPath.resolve(ORIGINALFILE_SUBDIRECTORY);
		filePath = Files.list(originalStoreFolderPath).
				filter(f -> !f.getFileName().toString().equals(contentTypeFilename) ).
				reduce(null, (accumulator, f) -> f);
		
		if (filePath==null) {
			throw new IllegalStateException(new FileNotFoundException(uuid.toString()));
		}

		storedFile.setName(filePath.getFileName().toString());
		storedFile.setSize(filePath.toFile().length());
		storedFile.setPath(filePath);
		
		contentTypeFile = uuidFolderPath.resolve(contentTypeFilename);
		storedFile.setContentType(Files.readString(contentTypeFile, StandardCharsets.UTF_8));
		
		return storedFile;
	}


}
