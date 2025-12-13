package fr.sandro642.orbit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OrbitHelper {

    protected void getLatestHashCommit(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new java.net.URI(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);

            if (!jsonArray.isEmpty()) {
                String latestCommitHash = jsonArray.get(0).getAsJsonObject()
                        .get("sha").getAsString();
                System.out.println("Latest Commit Hash: " + latestCommitHash);
            } else {
                System.out.println("No commits found.");
            }

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }
    }
}
