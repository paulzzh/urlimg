package com.paulzzh.urlimg;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Image<I> {
    private String hash;
    private I image;
    private int width;
    private int height;

    public Image(String hash, I image, int width, int height, int maxHeight) {
        this.hash = hash;
        this.image = image;
        BigDecimal b = new BigDecimal((float) height / width);
        double hx = b.setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (width > 300) {
            this.width = 300;
            this.height = (int) (300 * hx);
        }
        if (height > maxHeight) {
            this.height = maxHeight;
            this.width = (int) (maxHeight / hx);
        }
    }

    public String getHash() {
        return this.hash;
    }

    public I getImage() {
        return this.image;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
