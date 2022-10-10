package com.example.demo.controller;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import com.example.demo.response.ResponseMessage;
import com.example.demo.services.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/email")
public class EmailController {

	@Autowired
	private JavaMailSender jMailSender;

	@Autowired
	FilesStorageService storageService;

	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
		String message = "";
		String fileName = "Srikanth.pdf";
		String filePath = "C:\\Users\\Adithya Reddy\\Downloads\\EGA\\POC\\demo\\uploads\\" + fileName;
		
		try {
			storageService.saveFile(file, fileName);
			sendEmailAttachment(filePath, fileName);
			clearDocs();
			message = "Uploaded the file successfully: " + fileName;
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
		} catch (Exception e) {
			clearDocs();
			message = "Could not upload the file: " + fileName + "!";
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
	}
	
	private void sendEmailAttachment(String filePath, String fileName) throws IOException {
		MimeMessage message = jMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo("aksharaadithya06@gmail.com");
			helper.setText("<html><body><h1>hello Welcome!</h1><body></html>", true);
			FileSystemResource file1 = new FileSystemResource(filePath);
			System.out.println("File Name == " + "file" + " " + filePath);
			helper.addAttachment(fileName, file1);
			helper.setSubject("Document from Srikanth");
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		jMailSender.send(message);
	}

	private void clearDocs() {
		storageService.deleteAll();
		storageService.init();
	}
}
