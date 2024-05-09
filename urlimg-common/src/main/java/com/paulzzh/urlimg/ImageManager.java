package com.paulzzh.urlimg;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;

public interface ImageManager {
    Map<String, Image> cache = Collections.synchronizedMap(new HashMap<>());
    Map<String, List<Image>> images = Collections.synchronizedMap(new HashMap<>());

    static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    static Image getImage(String hash) {
        return cache.get(hash);
    }

    static List<Image> getImages(String hash) {
        return images.get(hash);
    }

    default void addImages(List<String> list) {
        new Thread(() -> {
            List<Image> imgs = new ArrayList<>();
            for (String url : list) {
                Image img = addImage(url);
                if (img != null) {
                    imgs.add(img);
                }
            }
            if (!imgs.isEmpty()) {
                final String uuid = UUID.randomUUID().toString().replace("-", "");
                images.put(uuid, imgs);
                showImages(uuid);
            }
        }).start();
    }

    default Image addImage(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String hash = bytesToHex(md.digest(url.getBytes(StandardCharsets.UTF_8)));
            Path path = Paths.get("urlimgcache/" + hash + ".png");
            Files.createDirectories(Paths.get("urlimgcache/"));
            if (cache.containsKey(hash)) {
                return cache.get(hash);
            } else if (path.toFile().isFile()) {
                Image img = readImage(hash, Files.newInputStream(path));
                if (img == null) {
                    img = readImage(hash, new URL(url).openStream());
                    if (img != null) {
                        saveImage(img, path);
                    }
                }
                if (img != null) {
                    cache.put(hash, img);
                    return cache.get(hash);
                }
            } else {
                Image img = readImage(hash, new URL(url).openStream());
                if (img != null) {
                    saveImage(img, path);
                    cache.put(hash, img);
                    return cache.get(hash);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    Image readImage(String hash, InputStream bin);

    void saveImage(Image obj, Path out);

    void showImages(String id);
}
