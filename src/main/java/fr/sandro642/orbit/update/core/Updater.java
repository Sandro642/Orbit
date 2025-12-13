package fr.sandro642.orbit.update.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import fr.sandro642.orbit.Orbit;
import fr.sandro642.orbit.update.Version;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Updater {

    private static final Updater INSTANCE = new Updater();

    public int statusBarProgress = 0;

    public void checkForUpdates() {
        try {
            statusBarProgress = 1;

            Class<?> classReference = Updater.class;

            URL url = classReference.getProtectionDomain().getCodeSource().getLocation();

            Path filePath = Paths.get(url.toURI());
            Path FolderParent = filePath.getParent();

            if (isLatestVersion() == true) {
                downloadFile("https://github.com/Sandro642/sandro642.github.io/blob/main/orbit/jar/fr/sandro642/orbit/Orbit/" + fetchVersion() + "/Orbit-" + fetchVersion() + "-fat.jar", FolderParent.toString() + "/Orbit-" + fetchVersion() + ".jar");
            }

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }
    }

    private String fetchVersion() {
        try {
            statusBarProgress = 2;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/Sandro642/Orbit/tags"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            JsonArray data = gson.fromJson(response.body(), JsonArray.class);

            if (data != null && !data.isEmpty()) {
                return data.get(0).getAsJsonObject().get("name").getAsString();
            } else {
                return "No tags found";
            }

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
            return "Error";
        }
    }

    private boolean isLatestVersion() {
        String ORBIT = "\u001B[94m[Orbit] \u001B[0m";
        String fetchedVersion = fetchVersion();

        if (!fetchedVersion.equals(Version.VERSION)) {
            Orbit.getInstance().getLogger().INFO(ORBIT + "A new version is available: " + fetchedVersion + " (You are using " + Version.VERSION + ")");
            statusBarProgress = 3;

            return true;
        } else {
            Orbit.getInstance().getLogger().INFO(ORBIT + "You are using the latest version: " + Version.VERSION);

            return false;
        }
    }

    private void downloadFile(String urlStr, String filePath) {
        try {
            statusBarProgress = 4;

            URL url = new URL(urlStr);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());

            FileOutputStream fos = new FileOutputStream(filePath);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }
    }

    private Mono<Void> removeAndStartNewVersion() {
        return Mono.fromRunnable(() -> {
            try {
                Class<?> classReference = Updater.class;

                URL url = classReference.getProtectionDomain().getCodeSource().getLocation();

                Path filePath = Paths.get(url.toURI());
                Path FolderParent = filePath.getParent();

                String LOCAL_JAR = FolderParent.toString() + "/Orbit-" + fetchVersion() + ".jar";

            } catch (Exception exception) {
                Orbit.getInstance().getLogger().ERROR(exception.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public static Updater getUpdaterSingleton() {
        return INSTANCE;
    }
}
