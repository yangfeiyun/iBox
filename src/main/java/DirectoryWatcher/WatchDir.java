package DirectoryWatcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.HashMap;
import java.util.Map;

import CloudDriveSync.FileSyncManager;


//Watch a directory for changes to files
public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private boolean trace;
    private FileSyncManager fileSyncManager;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    //Register the given directory with the WatchService
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    //Register the given directory, and all its sub-directories, with the WatchService.
    private void registerAll(final Path start) throws IOException {
        //Register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    //Create a WatchService and register the given directory
    public WatchDir(final Path dir, FileSyncManager fileSyncManager) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.fileSyncManager = fileSyncManager;
        this.keys = new HashMap<>();

        registerAll(dir);

        //Enable trace after initial registration
        this.trace = true;
    }

    //Process all events for keys queued to the watcher
    @SuppressWarnings("rawtypes")
    public void processEvents() {
        while (true) {

            //Wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                //How OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                //Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                //Print out event
                System.out.format("%s: %s\n", event.kind().name(), child);

                //Sync to remote
                try {
                    if (event.kind() == ENTRY_CREATE) {
                        fileSyncManager.addFile(child.toFile());
                    } else if (event.kind() == ENTRY_MODIFY) {
                        fileSyncManager.updateFile(child.toFile());
                    } else if (event.kind() == ENTRY_DELETE) {
                        fileSyncManager.deleteFile(child.toFile());
                    }
                } catch (IOException e) {
                    System.out.println("Failed to sync the file to remote storage");
                    e.printStackTrace();
                }
            }

            //Reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                //All directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

}
