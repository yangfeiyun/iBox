import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import DirectoryWatcher.WatchDir;
import CloudDriveSync.GoogleDriveSyncManager;
import CloudDriveSync.GoogleDriveServiceProvider;
import CloudDriveSync.FileSyncManager;


public class main {

    static void usage() {
        System.err.println("Usage: Java DirectoryWatcher.WatchDir dir");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException {
        //Parse arguments
        if (args.length < 1) {
            usage();
        }

        //Create file sync manager
        FileSyncManager fileSyncManager = new GoogleDriveSyncManager(
                GoogleDriveServiceProvider.get().getGoogleDriveClient());

        //Register directory and process its events
        Path dir = Paths.get(args[0]);
        new WatchDir(dir, fileSyncManager).processEvents();
    }
}
