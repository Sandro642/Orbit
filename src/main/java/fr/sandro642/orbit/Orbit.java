package fr.sandro642.orbit;

import fr.sandro642.orbit.log.Logger;
import fr.sandro642.orbit.log.Logs;
import fr.sandro642.orbit.update.core.Updater;

import java.io.File;

public class Orbit extends OrbitHelper {

    // Make an instance of Orbit
    private static final Orbit INSTANCE = new Orbit();

    public static void main(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                if ("--delete-old".equals(args[i]) && (i + 1) < args.length) {
                    String oldJarPath = args[i + 1];
                    File oldFile = new File(oldJarPath);
                    Thread.sleep(2000);

                    if (oldFile.exists()) {
                        boolean deleted = oldFile.delete();
                        if (!deleted) {
                            System.err.println("Failed to delete old JAR: " + oldJarPath);
                        } else {
                            System.out.println("Old JAR deleted successfully: " + oldJarPath);
                        }
                    }
                    break;
                }
            }

            INSTANCE.getUpdater().checkForUpdates();

        } catch (Exception exception) {
            INSTANCE.getLogger().ERROR(exception.getMessage());
        }
    }

    // Get the singleton instance of Orbit
    public static Orbit getInstance() {
        return INSTANCE;
    }

    /**
     * Get the singleton instance of Logger
     *
     * @return Logger instance
     */
    public Logger getLogger() {
        return Logger.getLoggerSingleton();
    }

    /**
     * Get the singleton instance of Logs
     *
     * @return Logs instance
     */
    public Logs getLogs() {
        return Logs.getLogsSingleton();
    }

    public Updater getUpdater() {
        return Updater.getUpdaterSingleton();
    }
}