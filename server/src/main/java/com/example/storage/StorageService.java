package com.example.storage;

public interface StorageService {
    record UploadResult(String url, String remoteId) {}

    UploadResult upload(byte[] bytes, String filename, String contentType) throws Exception;

    default void delete(String remoteId) throws Exception {}
}
