package io.anuke.sanctre.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import io.anuke.ucore.core.Timers;

import java.nio.file.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Watcher {
    static WatchService watchService;
    static CopyOnWriteArrayList<PathListener> watchers = new CopyOnWriteArrayList<>();

    static {

        try {
            watchService
                    = FileSystems.getDefault().newWatchService();

            Thread thread = new Thread(() -> {
                try {
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path t = (Path)event.context();
                            for(PathListener l : watchers){
                                if(t.getFileName().equals(l.path.getFileName())){
                                    Timers.runTask(10f, () -> l.listener.accept(l.file));
                                }
                            }
                        }
                        key.reset();
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            });
            thread.setDaemon(true);
            thread.start();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void watch(FileHandle file, boolean delete, Consumer<FileHandle> listener){
        Path path = file.parent().file().toPath().toAbsolutePath();
        Path target = file.file().toPath().toAbsolutePath();

        try{
            if(delete){
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            }else{
                path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            }

            watchers.add(new PathListener(target, file, listener));

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static class PathListener{
        Path path;
        FileHandle file;
        Consumer<FileHandle> listener;

        PathListener(Path path, FileHandle file, Consumer<FileHandle> listener){
            this.path = path;
            this.file = file;
            this.listener = listener;
        }
    }
}
