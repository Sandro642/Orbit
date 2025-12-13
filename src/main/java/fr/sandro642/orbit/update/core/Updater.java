package fr.sandro642.orbit.update.core;

import fr.sandro642.orbit.Orbit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.FileWriter;
import java.security.CodeSource;
import java.sql.Ref;

public class Updater {

    private static final Updater INSTANCE = new Updater();

    public String latestCommitHashLocal = "";

    public void GitAutomationMain() {
        File workDir = null;
        try {
            workDir = getJarDirectory();

            if (!workDir.exists()) {
                workDir.mkdirs();
            }

            Git git = initializeOrLoadGitRepository(workDir);

            File newFile = createDummyFile(workDir, "auto-commit-info.txt");

            git.add()
                    .addFilepattern(".")
                    .call();

            RevCommit commitRef = git.commit()
                    .setMessage("Automated commit: Added auto-commit-info.txt")
                    .setAuthor("Orbit", "sandro33810@gmail.com")
                    .call();

            ObjectId commitId = commitRef.getId().toObjectId();

            latestCommitHashLocal = commitId.getName();

            git.close();

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }

    }

    private File getJarDirectory() {
        try {
            CodeSource codeSource = Orbit.class.getProtectionDomain().getCodeSource();

            if (codeSource != null && codeSource.getLocation() != null) {
                File jarFile = new File(codeSource.getLocation().toURI());

                if (jarFile.isDirectory()) {
                    return jarFile;
                } else {
                    return jarFile.getParentFile();
                }
            } else {
                throw new IllegalStateException("Unable to determine the JAR file location.");
            }

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }

        return new File(".");
    }

    private Git initializeOrLoadGitRepository(File directory) {
        try {
            File gitDir = new File(directory, ".git");

            if (!gitDir.exists()) {
                InitCommand initCommand = Git.init();
                initCommand.setDirectory(directory);
                return initCommand.call();
            } else {
                return Git.open(directory);
            }

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }
        return null;
    }

    private File createDummyFile(File directory, String filename) {
        try {
            File file = new File(directory, filename);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write("This is a dummy file for testing purposes.");
                return file;
            }

        } catch (Exception exception) {
            Orbit.getInstance().getLogger().ERROR(exception.getMessage());
        }
        return directory;
    }

    public static Updater getUpdaterSingleton() {
        return INSTANCE;
    }
}
