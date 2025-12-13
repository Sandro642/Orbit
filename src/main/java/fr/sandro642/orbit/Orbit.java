package fr.sandro642.orbit;

import fr.sandro642.orbit.log.Logger;
import fr.sandro642.orbit.log.Logs;

public class Orbit extends OrbitHelper {

    // Make an instance of Orbit
    private static final Orbit INSTANCE = new Orbit();

    // GITHUB API URL REPOSITORY INTEGRATION*
    private final String GITHUB_API_URL = "https://api.github.com/repos/Sandro642/Orbit/commits";

    public static void main(String[] args) {
        // 1. Get the latest commit hash from the GitHub API
        INSTANCE.getLatestHashCommit(INSTANCE.GITHUB_API_URL);


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
}
