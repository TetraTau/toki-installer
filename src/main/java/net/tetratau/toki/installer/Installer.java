package net.tetratau.toki.installer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Properties;

public class Installer {

    public static void transferPatchInfo(final Path sourcePaperclipPath, final Path destinationPaperclipPath, String version) {
        try (FileSystem sourceFileSystem = FileSystems.newFileSystem(URI.create("jar:file:" + sourcePaperclipPath.toAbsolutePath()), Map.of("create", "true"))) {
            try (FileSystem destFileSystem = FileSystems.newFileSystem(URI.create("jar:file:" + destinationPaperclipPath.toAbsolutePath()), Map.of("create", "true"))) {
                Files.copy(
                        sourceFileSystem.getPath("/META-INF/patches.list"),
                        destFileSystem.getPath("/META-INF/patches.list"),
                        StandardCopyOption.REPLACE_EXISTING
                );
                Files.copy(
                        sourceFileSystem.getPath("/META-INF/libraries.list"),
                        destFileSystem.getPath("/META-INF/libraries.list"),
                        StandardCopyOption.REPLACE_EXISTING
                );
                Files.copy(
                        sourceFileSystem.getPath("/META-INF/versions.list"),
                        destFileSystem.getPath("/META-INF/versions.list"),
                        StandardCopyOption.REPLACE_EXISTING
                );
                Files.copy(
                        sourceFileSystem.getPath("/META-INF/main-class"),
                        destFileSystem.getPath("/META-INF/main-class"),
                        StandardCopyOption.REPLACE_EXISTING
                );
                Files.copy(
                        sourceFileSystem.getPath("/META-INF/download-context"),
                        destFileSystem.getPath("/META-INF/download-context"),
                        StandardCopyOption.REPLACE_EXISTING
                );
                Files.walkFileTree(
                        sourceFileSystem.getPath("/META-INF/versions"),
                        new CopyFileVisitor(destFileSystem.getPath("/META-INF/versions"))
                );
                Files.walkFileTree(
                        sourceFileSystem.getPath("/META-INF/libraries"),
                        new CopyFileVisitor(destFileSystem.getPath("/META-INF/libraries"))
                );
                final Properties install = new Properties();
                install.load(new StringReader(Files.readString(destFileSystem.getPath("toki-install.properties"))));
                install.put("gameVersion", version);
                final StringWriter stringWriter = new StringWriter();
                install.list(new PrintWriter(stringWriter));
                Files.writeString(
                        destFileSystem.getPath("/toki-install.properties"),
                        stringWriter.getBuffer().toString()
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copied from stackoverflow lol
     */
    static final class CopyFileVisitor extends SimpleFileVisitor<Path> {
        private final Path targetPath;
        private Path sourcePath = null;
        public CopyFileVisitor(Path targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir,
                                                 final BasicFileAttributes attrs) throws IOException {
            if (sourcePath == null) {
                sourcePath = dir;
            } else {
                Files.createDirectories(targetPath.resolve(sourcePath
                        .relativize(dir)));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file,
                                         final BasicFileAttributes attrs) throws IOException {
            Files.copy(file,
                    targetPath.resolve(sourcePath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
            return FileVisitResult.CONTINUE;
        }
    }

}
