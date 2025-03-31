package com.awbd.lab3b.controllers;

import com.awbd.lab3b.services.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> uploadImage(@PathVariable String id, @RequestBody MultipartFile file) throws IOException {
        imageService.cacheImage(id, file.getBytes());
        return ResponseEntity.ok("Image Cached Successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable String id)  {
        imageService.deleteCachedImage(id);
        return ResponseEntity.ok("Image Deleted Successfully");
    }


    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        byte[] imageData = imageService.getCachedImage(id);
        if (imageData == null) {
            return ResponseEntity.notFound().build();
        }
        log.info("log img");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
    }


}
