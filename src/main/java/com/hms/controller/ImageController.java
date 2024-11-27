package com.hms.controller;

import com.hms.entity.AppUser;
import com.hms.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // -------------------- FileUpload --------------------- //

    @PostMapping(path = "/upload/file/{bucketName}",
                    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file,
                                        @PathVariable String bucketName,
                                        @RequestParam Long propertyId,
                                        @AuthenticationPrincipal AppUser user) {
        if (imageService.verifyProperties(propertyId)) {
            String imgUrl = imageService.uploadFile(file, bucketName);
            return new ResponseEntity<>(imageService.addUrl(imgUrl, propertyId, user), HttpStatus.OK);
        }
        return new ResponseEntity<>("Property Id is not Available", HttpStatus.OK);
    }

    // ------------------- GetFileLists -------------------- //

    @GetMapping("/list/{bucketName}")
    public ResponseEntity<List<String>> listFiles(@PathVariable String bucketName) {
        return new ResponseEntity<>(imageService.listFiles(bucketName), HttpStatus.OK);
    }

    // -------------------- FileDelete --------------------- //

    @DeleteMapping("/delete/{bucketName}")
    public ResponseEntity<String> deleteFile(@RequestParam(value = "url") String fileUrl,
                                             @PathVariable String bucketName) {
        try {
            imageService.deleteFile(bucketName, fileUrl);
            return new ResponseEntity<>("File marker as delete Successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deletePermanently/{bucketName}")
    public ResponseEntity<String> deleteFilePermanently(
            @PathVariable String bucketName,
            @RequestParam(value = "fileKey") String fileKey) {
        try {
            imageService.deleteFilePermanently(bucketName, fileKey);
            return new ResponseEntity<>("File and all versions deleted permanently", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
