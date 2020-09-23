package io.keikai.devref;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ExampleFileWalker {

    /**
     * scan from basePath recursively.
     */
    static public List<File> scanFolders(Path basePath) throws IOException {
        String[] dir2Skip = {"WEB-INF", "js"};
        List<File> folders = new LinkedList<>();
        Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (isDir2Skip(basePath.relativize(dir))) {
                    return FileVisitResult.SKIP_SUBTREE;
                } else {
                    folders.add(dir.toFile());
                    return FileVisitResult.CONTINUE;
                }
            }

            private boolean isDir2Skip(Path path) {
                for (String dir : dir2Skip) {
                    if (path.toString().endsWith(dir)) {
                        return true;
                    }
                }
                return false;
            }
        });
        Collections.sort(folders);
        return folders;
    }
}
