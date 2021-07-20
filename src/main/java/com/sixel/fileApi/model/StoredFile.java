package com.sixel.fileApi.model;

import java.nio.file.Path;
import java.util.UUID;

/**
 * @author msixe
 *
 */
public class StoredFile {
	private UUID uuid;
	private String name;
	private String contentType;
	private Long size;
	private Path path;
	
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(final UUID uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(final String name) {
		this.name = name;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(final Long size) {
		this.size = size;
	}
	public Path getPath() {
		return path;
	}
	public void setPath(final Path path) {
		this.path = path;
	}
	
}
