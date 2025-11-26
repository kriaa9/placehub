package com.corner.cornerbackend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String, Object> getSignature(Map<String, Object> params) {
        long timestamp = System.currentTimeMillis() / 1000;
        params.put("timestamp", timestamp);

        // Generate signature using Cloudinary utility
        String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

        return Map.of(
                "signature", signature,
                "timestamp", timestamp,
                "api_key", cloudinary.config.apiKey,
                "cloud_name", cloudinary.config.cloudName);
    }
}
