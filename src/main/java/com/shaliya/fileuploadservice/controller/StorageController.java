package com.shaliya.fileuploadservice.controller;


import com.shaliya.fileuploadservice.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/file")
public class StorageController {

  @Autowired
  private StorageService service;


  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {
    return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);
  }

  @PostMapping("/multiple-upload")
  public ResponseEntity<List<URL>> uploadMultipleFile(@RequestParam(value = "file") List<MultipartFile> file) {

    return new ResponseEntity<>(service.uploadMultipleFile(file), HttpStatus.OK);
  }

  @GetMapping("/download/{fileName}")
  public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
    byte[] data = service.downloadFile(fileName);
    ByteArrayResource resource = new ByteArrayResource(data);
    return ResponseEntity
      .ok()
      .contentLength(data.length)
      .header("Content-type", "application/octet-stream")
      .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
      .body(resource);
  }

  @DeleteMapping("/delete/{fileName}")
  public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
    return new ResponseEntity<>(service.deleteFile(fileName), HttpStatus.OK);
  }
}
