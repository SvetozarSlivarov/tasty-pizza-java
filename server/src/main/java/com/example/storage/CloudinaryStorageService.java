package com.example.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;

public class CloudinaryStorageService implements StorageService {
    private final Cloudinary cloud;

    public CloudinaryStorageService(String cloudName, String apiKey, String apiSecret) {
        this.cloud = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Override
    public UploadResult upload(byte[] bytes, String filename, String contentType) throws Exception {
        Map<String, Object> params = ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "products",
                "use_filename", true,
                "unique_filename", true
        );
        Map<?,?> up = cloud.uploader().upload(bytes, params);
        String url = (String) up.get("secure_url");
        String publicId = (String) up.get("public_id");
        return new UploadResult(url, publicId);
    }
}
