package com.corner.cornerbackend.controllers;

import com.corner.cornerbackend.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @GetMapping("/signature")
    public ResponseEntity<Map<String, Object>> getUploadSignature(@RequestParam(required = false) String folder) {
        Map<String, Object> params = new HashMap<>();

        // Optional: Enforce specific folder for uploads
        if (folder != null && !folder.isEmpty()) {
            params.put("folder", folder);
        } else {
            params.put("folder", "corner-uploads"); // Default folder
        }

        return ResponseEntity.ok(cloudinaryService.getSignature(params));
    }
}
