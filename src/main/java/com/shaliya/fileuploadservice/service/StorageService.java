package com.shaliya.fileuploadservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class StorageService {

  @Value("${application.bucket.name}")
  private String bucketName;

  @Autowired
  private AmazonS3 s3Client;

  public String uploadFile(MultipartFile file) {
    File fileObj = convertMultiPartFileToFile(file);
    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
    s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj).withCannedAcl(CannedAccessControlList.PublicRead));
    fileObj.delete();
    return "File uploaded : " + s3Client.getUrl(bucketName, fileName);
  }


  public byte[] downloadFile(String fileName) {
    String name = "https://visco-image-upload-sample.s3.us-east-2.amazonaws.com/1657631482781_Screenshot%20from%202022-02-14%2021-50-46.png";
    S3Object s3Object = s3Client.getObject(bucketName, name);
    System.out.println(s3Object.getObjectContent());
    S3ObjectInputStream inputStream = s3Object.getObjectContent();
    try {
      byte[] content = IOUtils.toByteArray(inputStream);
      System.out.println(content);
      return content;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  public String deleteFile(String fileName) {
    s3Client.deleteObject(bucketName, fileName);
    return fileName + " removed ...";
  }


  private File convertMultiPartFileToFile(MultipartFile file) {
    File convertedFile = new File(file.getOriginalFilename());
    try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
      fos.write(file.getBytes());
    } catch (IOException e) {
      log.error("Error converting multipartFile to file", e);
    }
    return convertedFile;
  }
}
