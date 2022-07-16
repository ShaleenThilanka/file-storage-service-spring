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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StorageService {

  @Value("${application.bucket.name}")
  private String bucketName;

  @Autowired
  private AmazonS3 s3Client;


  public List<URL> uploadMultipleFile(List<MultipartFile> file) {
    File fileObj;
    List<URL> returnImageList =new ArrayList<>();
    String fileName="";
    for (int i=0; i<file.size(); i++){
      fileObj=convertMultiPartFileToFile(file.get(i));
      fileName = System.currentTimeMillis() + "_" + file.get(i).getOriginalFilename();
      s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj).withCannedAcl(CannedAccessControlList.PublicRead));
      fileObj.delete();
      returnImageList.add(s3Client.getUrl(bucketName, fileName));
    }

    return  returnImageList;
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
