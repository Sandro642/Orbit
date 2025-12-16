package fr.sandro642.orbit.update.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import fr.sandro642.orbit.Orbit;
import fr.sandro642.orbit.update.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
                downloadFile("https://raw.githubusercontent.com/Sandro642/sandro642.github.io/main/orbit/jar/fr/sandro642/orbit/Orbit/" + fetchVersion() + "/Orbit-" + fetchVersion() + "-fat.jar", FolderParent.toString() + "/Orbit-" + fetchVersion() + ".jar");

                removeAndStartNewVersion();
            }

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }
    }

    private String fetchVersion() {
        try {
            //final String PAT_KEY = "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";
            final String API_URL = "https://api.github.com/repos/Sandro642/Orbit/tags";

            statusBarProgress = 2;

            String authorizationHeader = "Bearer " + PAT_KEY;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    //.header("Authorization", authorizationHeader)
                    .GET()
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

            try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                 FileOutputStream fos = new FileOutputStream(filePath)) {

                long bytesTransferred = fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                fos.flush();
                fos.getFD().sync();

                System.out.println("Downloaded " + bytesTransferred + " bytes to " + filePath);
            }

            Thread.sleep(500);

            File downloadedFile = new File(filePath);
            if (!downloadedFile.exists() || downloadedFile.length() == 0) {
                throw new IOException("Download failed: file is missing or empty");
            }

            System.out.println("File downloaded successfully: " + downloadedFile.length() + " bytes");

        } catch (Exception exception) {
            exception.printStackTrace();
            Orbit.getInstance().getLogger().ERROR("Download error: " + exception.getMessage());

            // Supprimer le fichier corrompu s'il existe
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void removeAndStartNewVersion() {
        try {
            Class<?> classReference = Updater.class;

            URL url = classReference.getProtectionDomain().getCodeSource().getLocation();

            Path filePath = Paths.get(url.toURI());
            Path FolderParent = filePath.getParent();

            File LOCAL_JAR = new File(FolderParent.toString() + "/Orbit-" + Version.VERSION + ".jar");
            String newJarPath = FolderParent + "/Orbit-" + fetchVersion() + ".jar";

            System.out.println("Starting new version...");
            System.out.println("Current JAR: " + LOCAL_JAR.getAbsolutePath());
            System.out.println("New JAR: " + newJarPath);
            System.out.println("Command: java -jar " + newJarPath + " --delete-old " + LOCAL_JAR.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-jar",
                    newJarPath,
                    "--delete-old",
                    LOCAL_JAR.getAbsolutePath()
            );

            // IMPORTANT : Rediriger les sorties pour voir les erreurs
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();

            // Attendre un peu et vérifier si le processus tourne toujours
            Thread.sleep(2000);

            if (process.isAlive()) {
                System.out.println("New version is running successfully!");
                System.exit(0);
            } else {
                System.err.println("ERROR: New process exited with code: " + process.exitValue());
                System.err.println("The new version failed to start. Check the output above.");
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }
    }

    public static Updater getUpdaterSingleton() {
        return INSTANCE;
    }
}
