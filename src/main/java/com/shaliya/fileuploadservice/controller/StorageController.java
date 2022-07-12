package com.shaliya.fileuploadservice.controller;


import com.shaliya.fileuploadservice.service.SMSService;
import com.shaliya.fileuploadservice.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/file")
public class StorageController {

  @Autowired
  private StorageService service;



  @PostMapping("/test")
  public String test() {
    try {
      // Construct data
      String apiKey = "apikey=" + "yourapiKey";
      String message = "&message=" + "This is your message";
      String sender = "&sender=" + "TXTLCL";
      String numbers = "&numbers=" + "918123456789";

      // Send data
      HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
      String data = numbers + message + sender;
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
      conn.getOutputStream().write(data.getBytes("UTF-8"));
      final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      final StringBuffer stringBuffer = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        stringBuffer.append(line);
      }
      rd.close();

      return stringBuffer.toString();
    } catch (Exception e) {
      System.out.println("Error SMS "+e);
      return "Error "+e;
    }
//    System.out.println("hello");


  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {

    return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);
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
