package io.sammers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Auth {

    public static String getTokenFromAuthEndpoint(String username, String password, String uri) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{"
                    + " \"username\": \"" + username + "\",\n"
                    + " \"password\": \"" + password + "\"\n"
                    + "}'"))
                .build();
            HttpResponse<String> response = HttpClient.newBuilder().build()
                .send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().split(":")[1].replaceAll("\"", "").replaceAll("}", "");
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
