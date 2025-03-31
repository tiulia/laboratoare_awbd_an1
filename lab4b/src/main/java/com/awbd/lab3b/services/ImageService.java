package com.awbd.lab3b.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImageService {
    private final String IMAGE_PREFIX = "image:";

    private final RedisTemplate<String, byte[]> redisTemplate;


    public ImageService(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheImage(String imageId, byte[] imageData) {
        redisTemplate.opsForValue().set(IMAGE_PREFIX + imageId, imageData);
    }

    public byte[] getCachedImage(String imageId) {

        return redisTemplate.opsForValue().get(IMAGE_PREFIX + imageId);
    }

    public void deleteCachedImage(String imageId) {

        redisTemplate.delete(IMAGE_PREFIX + imageId);
    }
}
