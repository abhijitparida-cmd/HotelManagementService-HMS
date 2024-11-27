package com.hms.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.hms.entity.AppUser;
import com.hms.entity.Images;
import com.hms.entity.Property;
import com.hms.payload.AppUserDto;
import com.hms.payload.ImagesDto;
import com.hms.payload.PropertyDto;
import com.hms.repository.ImagesRepository;
import com.hms.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private AmazonS3 amazonS3;

    private final PropertyRepository propertyRepository;
    private final ImagesRepository imagesRepository;
    private final PropertiesService propertiesService;
    private final AppUserService appUserService;

    public ImageService(PropertyRepository propertyRepository, ImagesRepository imagesRepository, PropertiesService propertiesService, AppUserService appUserService) {
        this.propertyRepository = propertyRepository;
        this.imagesRepository = imagesRepository;
        this.propertiesService = propertiesService;
        this.appUserService = appUserService;
    }

    // -------------------- FileUpload --------------------- //

    public boolean verifyProperties(Long propertyId) {
        return propertyRepository.existsById(propertyId);
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);
            try {
                amazonS3.putObject(bucketName, convFile.getName(), convFile);
                return amazonS3.getUrl(bucketName, file.getOriginalFilename()).toString();
            } catch (AmazonS3Exception s3Exception) {
                return "Unable to upload file :" + s3Exception.getMessage();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload file", e);
        }
    }

    public ImagesDto addUrl(String imgUrl, Long propertyId, AppUser user) {

        Property property = propertyRepository.findById(propertyId).get();

        Images images = new Images();
        images.setImgUrl(imgUrl);
        images.setPropertyId(property);
        images.setAppUserId(user);
        Images savedImage = imagesRepository.save(images);

        PropertyDto propertyDto = propertiesService.convertEntityToDto(property);
        AppUserDto appUserDto = appUserService.mapToDto(user);

        ImagesDto imagesDto = new ImagesDto();
        imagesDto.setImageUrl(savedImage.getImgUrl());
        imagesDto.setPropertyDto(propertyDto);
        imagesDto.setAppUserDto(appUserDto);
        return imagesDto;
    }

    // ------------------- GetFileLists -------------------- //

    public List<String> listFiles(String bucketName) {
        List<String> fileUrls = new ArrayList<>();

        try {
            ObjectListing objectListing = amazonS3.listObjects(bucketName);
            for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
                String fileUrl = amazonS3.getUrl(bucketName, summary.getKey()).toString();
                fileUrls.add(fileUrl);
            }
        } catch (AmazonS3Exception e) {
            throw new RuntimeException("Error fetching files from S3: " + e.getMessage());
        }
        return fileUrls;
    }

    // -------------------- FileDelete --------------------- //

    public void deleteFile(String bucketName, String fileUrl) {
        String urlPrefix = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, amazonS3.getRegionName());
        if (!fileUrl.startsWith(urlPrefix)) {
            throw new IllegalArgumentException("Invalid file URL format for the specified bucket.");
        }
        String fileKey = fileUrl.substring(urlPrefix.length());
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileKey));
    }

    public void deleteFilePermanently(String bucketName, String fileKey) {
        ListVersionsRequest listVersionsRequest = new ListVersionsRequest()
                .withBucketName(bucketName)
                .withPrefix(fileKey);
        VersionListing versionListing = amazonS3.listVersions(listVersionsRequest);
        for (S3VersionSummary versionSummary : versionListing.getVersionSummaries()) {
            String versionId = versionSummary.getVersionId();
            amazonS3.deleteVersion(bucketName, versionSummary.getKey(), versionId);
        }
    }
}
