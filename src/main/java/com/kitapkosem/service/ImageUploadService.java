package com.kitapkosem.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.Part;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ImageUploadService {

    private static final String IMGBB_API_KEY = "SENIN_IMGBB_API_ANAHTARIN";
    private static final String IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload";

    public String uploadImageToImgBB(Part filePart) throws IOException {
        if (filePart == null || filePart.getSize() == 0
                || filePart.getSubmittedFileName() == null || filePart.getSubmittedFileName().trim().isEmpty()) {
            System.out.println("ImageUploadService: Yüklenecek geçerli bir dosya bulunamadı.");
            return null;
        }

        String imageUrl = null;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(15000)
                .setConnectionRequestTimeout(15000)
                .setSocketTimeout(15000)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build(); InputStream fileInputStream = filePart.getInputStream()) {

            HttpPost uploadFile = new HttpPost(IMGBB_UPLOAD_URL);

            String fileName = filePart.getSubmittedFileName();
            ContentType imageContentType = ContentType.DEFAULT_BINARY;
            String partContentTypeStr = filePart.getContentType();
            if (partContentTypeStr != null && partContentTypeStr.startsWith("image/")) {
                try {
                    imageContentType = ContentType.create(partContentTypeStr);
                } catch (Exception e) {
                    System.err.println("ImageUploadService: Geçersiz ContentType string'i: " + partContentTypeStr + ". Fallback kullanılıyor.");
                    imageContentType = ContentType.APPLICATION_OCTET_STREAM;
                }
            } else {
                System.out.println("ImageUploadService: Algılanan ContentType bir resim değil veya null: " + partContentTypeStr + ". APPLICATION_OCTET_STREAM kullanılıyor.");
                imageContentType = ContentType.APPLICATION_OCTET_STREAM;
            }
            System.out.println("ImageUploadService: Kullanılan ContentType: " + imageContentType.getMimeType());

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(StandardCharsets.UTF_8);
            builder.addTextBody("key", IMGBB_API_KEY, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
            builder.addBinaryBody(
                    "image",
                    fileInputStream,
                    imageContentType,
                    fileName
            );

            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);

            System.out.println("ImageUploadService: imgBB'ye istek gönderiliyor (Dosya: " + fileName + ", Boyut: " + filePart.getSize() + ")...");

            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseString = (responseEntity != null) ? EntityUtils.toString(responseEntity) : null;

                System.out.println("ImageUploadService: imgBB yanıt kodu: " + statusCode);
                if (responseString != null) {
                    System.out.println("ImageUploadService: imgBB ham yanıtı: " + responseString.substring(0, Math.min(responseString.length(), 500)) + "...");
                }

                if (statusCode == 200 && responseString != null) {
                    try {
                        JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
                        if (jsonObject.has("data") && jsonObject.getAsJsonObject("data").has("url")) {
                            imageUrl = jsonObject.getAsJsonObject("data").get("url").getAsString();
                            System.out.println("ImageUploadService: Yüklenen resim URL'i: " + imageUrl);
                        } else if (jsonObject.has("status_code") && jsonObject.get("status_code").getAsInt() != 200 && jsonObject.has("error") && jsonObject.getAsJsonObject("error").has("message")) {
                            System.err.println("ImageUploadService: imgBB API Hata: " + jsonObject.getAsJsonObject("error").get("message").getAsString() + " (Kod: " + jsonObject.get("status_code").getAsInt() + ")");
                        } else if (jsonObject.has("error") && jsonObject.getAsJsonObject("error").has("message")) {
                            System.err.println("ImageUploadService: imgBB API Hata (detaysız): " + jsonObject.getAsJsonObject("error").get("message").getAsString());
                        } else {
                            System.err.println("ImageUploadService: imgBB yanıtında 'data.url' bulunamadı. Yanıt: " + responseString);
                        }
                    } catch (Exception e) {
                        System.err.println("ImageUploadService: imgBB JSON yanıtı parse edilirken hata: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("ImageUploadService: imgBB'ye yükleme başarısız. Yanıt Kodu: " + statusCode);
                    if (responseString != null) {
                        System.err.println("ImageUploadService: Hata Detayı (yanıttan): " + responseString);
                    }
                }
                EntityUtils.consume(responseEntity);
            }

        } catch (IOException e) {
            System.err.println("ImageUploadService: HTTP isteği veya dosya okuma sırasında I/O Hatası: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return imageUrl;
    }
}
