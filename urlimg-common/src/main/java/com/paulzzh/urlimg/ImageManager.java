package com.paulzzh.urlimg;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class ImageManager {
    private static final Map<String, Image<?>> cache = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, List<Image<?>>> images = Collections.synchronizedMap(new HashMap<>());
    private static final List<String> hashes = Collections.synchronizedList(new ArrayList<>());
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ExecutorService executor2 = Executors.newCachedThreadPool();

    public static String bytesToHex(byte[] hash) {
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

    public abstract Image<?> readImage(String hash, InputStream in);

    public abstract Image<?> readAndSaveImage(String hash, InputStream in, File out);

    public abstract void showImages(String id);

    public List<Image<?>> getImages(String hash) {
        synchronized (images) {
            return images.get(hash);
        }
    }

    public void addImages(List<String> list) {
        executor.submit(() -> {
            List<Image<?>> imgs = new ArrayList<>();
            List<Future<Image<?>>> futures = new ArrayList<>();
            for (String url : list) {
                futures.add(executor2.submit(() -> addImage(url)));
            }
            for (Future<Image<?>> future : futures) {
                try {
                    Image<?> img = future.get();
                    if (img != null) {
                        imgs.add(img);
                    }
                } catch (Exception ignored) {

                }
            }
            if (!imgs.isEmpty()) {
                final String uuid = UUID.randomUUID().toString().replace("-", "");
                synchronized (images) {
                    images.put(uuid, imgs);
                }
                showImages(uuid);
            }
        });
    }

    public Image<?> addImage(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String hash = bytesToHex(md.digest(url.getBytes(StandardCharsets.UTF_8)));
            File out = Paths.get("urlimgcache/" + hash + ".png").toFile();
            Files.createDirectories(Paths.get("urlimgcache/"));
            synchronized (hashes) {
                if (hashes.contains(hash)) {
                    hash = hashes.get(hashes.indexOf(hash));
                } else {
                    hashes.add(hash);
                }
            }
            synchronized (hash) {
                synchronized (cache) {
                    if (cache.containsKey(hash)) {
                        return cache.get(hash);
                    }
                }
                Image<?> img;
                if (out.isFile()) {
                    img = readImage(hash, new FileInputStream(out));
                } else {
                    URL u = new URL(url);
                    URLConnection conn = u.openConnection();
                    if (conn instanceof HttpURLConnection || conn instanceof HttpsURLConnection) {
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(10000);
                        conn.setRequestProperty("User-Agent", "urlimg");
                        img = readAndSaveImage(hash, conn.getInputStream(), out);
                    } else {
                        throw new MalformedURLException("no protocol: " + u.getProtocol());
                    }
                }
                if (img != null) {
                    synchronized (cache) {
                        cache.put(hash, img);
                    }
                }
                return img;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
