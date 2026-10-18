package etu.ecole.fluxrssmobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtility {

    // Délais maximaux pour éviter qu'une image bloque le chargement du flux
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 15_000;

    // récupération d'une image sous format Bitmap à partir d'une url
    public static Bitmap getBitmapFromUrl(String imageURL) {
        if (imageURL == null) return null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(imageURL).openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.connect();
            // try-with-resources : le flux est fermé même en cas d'erreur
            try (InputStream input = connection.getInputStream()) {
                return BitmapFactory.decodeStream(input);
            }
        } catch (IOException e) {
            Log.e("Image", "Error getting BitMap" + e);
            return null;
        } finally {
            // libère la connexion dans tous les cas
            if (connection != null) connection.disconnect();
        }
    }

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] bytes) {
        if (bytes == null) return null;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
