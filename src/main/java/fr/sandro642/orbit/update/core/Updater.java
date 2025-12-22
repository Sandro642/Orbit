package fr.sandro642.orbit.update.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import fr.sandro642.orbit.Orbit;
import fr.sandro642.orbit.app.ui.MainFrame;
import fr.sandro642.orbit.repository.DatabaseManager;
import fr.sandro642.orbit.update.Version;

import javax.swing.*;
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

    public void checkForUpdates() {
        try {
            Orbit.getInstance().getFrame().init();

            Orbit.getInstance().getFrame().textComponent("Vérification des mises à jour...");
            Orbit.getInstance().getFrame().ProgressValue(1);

            Thread.sleep(1000);

            Class<?> classReference = Updater.class;

            URL url = classReference.getProtectionDomain().getCodeSource().getLocation();

            Path filePath = Paths.get(url.toURI());
            Path FolderParent = filePath.getParent();

            if (isLatestVersion()) {
                downloadFile("https://raw.githubusercontent.com/Sandro642/sandro642.github.io/main/orbit/jar/fr/sandro642/orbit/Orbit/" + fetchVersion() + "/Orbit-" + fetchVersion() + "-fat.jar", FolderParent.toString() + "/Orbit-" + fetchVersion() + ".jar");
                removeAndStartNewVersion();
            }

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }
    }

    private String fetchVersion() {
        try {
            final String API_URL = "https://api.github.com/repos/Sandro642/Orbit/tags";

            //final String authorizationHeader = "Bearer " + TOKEN;
            Thread.sleep(1000);

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
        try {
            String ORBIT = "\u001B[94m[Orbit] \u001B[0m";
            String fetchedVersion = fetchVersion();

            if (!fetchedVersion.equals(Version.VERSION)) {
                Orbit.getInstance().getLogger().INFO(ORBIT + "A new version is available: " + fetchedVersion + " (You are using " + Version.VERSION + ")");

                Orbit.getInstance().getFrame().textComponent("Nouvelle version disponible: " + fetchedVersion);
                Orbit.getInstance().getFrame().ProgressValue(3);
                Thread.sleep(1000);

                return true;
            } else {
                Orbit.getInstance().getLogger().INFO(ORBIT + "You are using the latest version: " + Version.VERSION);

                Orbit.getInstance().getFrame().textComponent("Vous utilisez la dernière version.");
                Orbit.getInstance().getFrame().ProgressValue(5);
                Thread.sleep(1000);
                Orbit.getInstance().getFrame().textComponent("Lancement de Orbit...");

                Orbit.getInstance().getFrame().kill();

                new DatabaseManager().init();
                SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));


                //System.exit(1);
            }

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }
        return false;
    }

    private void downloadFile(String urlStr, String filePath) {
        try {
            Orbit.getInstance().getFrame().textComponent("Téléchargement de la nouvelle version...");
            Orbit.getInstance().getFrame().ProgressValue(4);
            Thread.sleep(1000);

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

            Orbit.getInstance().getFrame().textComponent("Téléchargement terminé avec succès.");
            Orbit.getInstance().getFrame().ProgressValue(4);

        } catch (Exception exception) {
            exception.printStackTrace();
            Orbit.getInstance().getLogger().ERROR("Download error: " + exception.getMessage());

            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void removeAndStartNewVersion() {
        try {

            Orbit.getInstance().getFrame().textComponent("Lancement de la nouvelle version...");
            Orbit.getInstance().getFrame().ProgressValue(5);
            Thread.sleep(1000);

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

            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();

            Thread.sleep(2000);

            if (process.isAlive()) {

                Orbit.getInstance().getFrame().textComponent("Fermeture de Orbit Updater...");
                Orbit.getInstance().getFrame().ProgressValue(5);
                Thread.sleep(1000);

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
