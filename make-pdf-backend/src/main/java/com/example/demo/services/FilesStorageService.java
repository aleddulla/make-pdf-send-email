package com.example.demo.services;

import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
	public void init();

    public void deleteAll();

	public void save(MultipartFile file);
	
	public void saveFile(MultipartFile file, String fileName);
}
