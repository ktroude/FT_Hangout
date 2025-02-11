package dev.ktroude.ft_hangout.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utility class for handling image-related operations such as encoding,
 * decoding, resizing, and selecting images from the gallery.
 */
public class ImageHelper {

    /**
     * Converts a Bitmap image to a Base64 encoded string.
     *
     * @param bitmap The Bitmap to encode.
     * @return The Base64 encoded string.
     */
    public static String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Decodes a Base64 encoded string to a Bitmap image.
     *
     * @param base64String The Base64 string to decode.
     * @return The decoded Bitmap image.
     */
    public static Bitmap decodeBase64ToImage(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Resizes a Bitmap image while maintaining aspect ratio.
     *
     * @param bitmap The Bitmap to resize.
     * @param maxSize The maximum size for width or height.
     * @return The resized Bitmap.
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    /**
     * Launches an intent to pick an image from the gallery.
     *
     * @param activity The activity that initiates the image selection.
     * @param requestCode The request code to identify the result.
     */
    public static void selectImageFromGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Handles the result of image selection and returns the Base64 encoded string.
     *
     * @param activity The activity receiving the result.
     * @param data The Intent containing the selected image data.
     * @return The Base64 encoded string of the selected image.
     * @throws IOException If an error occurs while processing the image.
     */
    public static String handleImageResult(Activity activity, Intent data) throws IOException {
        if (data == null) {
            return "";
        }
        Uri selectedImageUri = data.getData();
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImageUri);
        return encodeImageToBase64(resizeBitmap(bitmap, 300));
    }
}

