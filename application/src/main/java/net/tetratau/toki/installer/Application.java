package net.tetratau.toki.installer;

import java.nio.file.Path;

public class Application {
    public static void main(String[] args) {
        Installer.transferPatchInfo(Path.of(args[0]), Path.of(args[1]), args[2]);
    }
}
