package com.example.gazcontorapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ValidationUtils {
    public static boolean isValidContract(String contract) {
        return contract.matches("\\d{3}/\\d{10}");
    }

    public static boolean isValidMeter(String meter) {
        return meter.matches("\\d{8}");
    }

    public static boolean isValidMeterReading(String input) {
        return input.matches("^\\d{5},\\d{3}$");
    }

    public static boolean isValidPhoto(Bitmap bitmap) {
        if (bitmap == null) return false;

        // Валидация размера изображения
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width < 800 || height < 600) { // Min size
            return false;
        }

        // Валидация размера файла в байтах
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        int sizeInBytes = stream.toByteArray().length;
        int maxFileSize = 5 * 1024 * 1024; // 5 MB
        if (sizeInBytes > maxFileSize) {
            return false;
        }

        return true; // Фото прошло валидацию
    }
}

