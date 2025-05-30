package org.example.emailprojectjavafx.utils.services;

import com.google.gson.Gson;
import org.example.emailprojectjavafx.models.Auth.AuthResponse;
import org.example.emailprojectjavafx.models.Auth.LoginRequest;
import org.example.emailprojectjavafx.models.BaseResponse;
import org.example.emailprojectjavafx.models.GenericPetition;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;
import static org.example.emailprojectjavafx.utils.services.TokenUtils.token;
import static org.example.emailprojectjavafx.utils.services.TokenUtils.tokenPath;

public class ServiceUtils {
    public static final String SERVER = "https://gloriaarnau.site";
    private static Gson gson = new Gson();

    public static boolean login(String username, String password) {
        try {
            String credentials = new Gson().toJson(new LoginRequest(username, password));
            String jsonResponse = getResponse(SERVER + "/auth/login", credentials, "POST");

            AuthResponse authResponse = new Gson().fromJson(jsonResponse, AuthResponse.class);
            if (authResponse != null && authResponse.isOk()) {
                TokenUtils.setToken(authResponse.getToken());
                TokenUtils.saveToken(authResponse.getToken(), tokenPath);
                TokenUtils.decodeToken();
                return true;
            }

        } catch (Exception e) {
            System.out.println("WRONG LOGIN");
        }
        return false;
    }

    /**
     * Generic method to make a petition to the api, uses consumer to
     * @param <T>
     */
    public static <T extends BaseResponse> void makePetition(GenericPetition<T> petition){
        String url = ServiceUtils.SERVER + "/" + petition.getModel() + "/" + petition.getPetition();
        ServiceUtils.getResponseAsync(url, petition.getData(), petition.getMethod())
                .thenApply(json -> gson.fromJson(json, petition.getResponseClass())
                ).thenAccept(response -> {
                    if (response.isOk()) {
                        petition.getOnSuccess().accept(response);
                    } else {
                        System.out.println(response.getError());
                        showAlert("Error", response.getError(), 2);
                    }
                }).exceptionally(_ -> {
                    System.out.println(petition.getErrorMessage());
                    showAlert("Error", petition.getErrorMessage(), 2);
                    return null;
                });
    }


    // Get charset encoding (UTF-8, ISO,...)
    public static String getCharset(String contentType) {
        for (String param : contentType.replace(" ", "").split(";")) {
            if (param.startsWith("charset=")) {
                return param.split("=", 2)[1];
            }
        }
        return null; // Probably binary content
    }

    public static String getResponse(String url, String data, String method) throws Exception {
        BufferedReader bufInput = null;
        StringJoiner result = new StringJoiner("\n");
        try {
            URL urlConn = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlConn.openConnection();
            conn.setReadTimeout(20000 /*milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(method);

            //conn.setRequestProperty("Host", "localhost");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
            conn.setRequestProperty("Accept-Language", "es-ES,es;q=0.8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");

            // If set, send the authentication token
            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            if (data != null) {
                /*
                if(image != null){
                    String boundary = Long.toHexString(System.currentTimeMillis());
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                }*/
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                conn.setDoOutput(true);
                //Send request
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(data.getBytes());
                wr.flush();
                wr.close();
            }
            int responseCode = conn.getResponseCode();

            String charset = getCharset(conn.getHeaderField("Content-Type"));

            if (charset != null) {
                InputStream input;
                if(responseCode >= 200 && responseCode < 400)
                    input = conn.getInputStream();
                else {
                    input = conn.getErrorStream();
                }
                if ("gzip".equals(conn.getContentEncoding())) {
                    input = new GZIPInputStream(input);
                }

                bufInput = new BufferedReader(
                        new InputStreamReader(input));

                String line;
                while ((line = bufInput.readLine()) != null) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            throw new Exception("ERROR");
        } finally {
            if (bufInput != null) {
                try {
                    bufInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result.toString();
    }

    public static CompletableFuture<String>
    getResponseAsync(String url, String data, String method) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getResponse(url, data, method);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
