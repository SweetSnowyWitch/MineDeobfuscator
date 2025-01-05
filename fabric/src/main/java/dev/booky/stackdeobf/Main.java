package dev.booky.stackdeobf;

import dev.booky.stackdeobf.mappings.CachedMappings;
import dev.booky.stackdeobf.mappings.providers.AbstractMappingProvider;
import dev.booky.stackdeobf.mappings.providers.QuiltMappingProvider;
import dev.booky.stackdeobf.util.VersionData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final VersionData VERSION_DATA = VersionData.fromClasspath();
    private static AbstractMappingProvider mappingProvider;
    private static CachedMappings mappings;

    public static void main(String[] args) throws IOException {
        mappingProvider = new QuiltMappingProvider(VERSION_DATA);
        List<Path> allFiles = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Path to directory: ");
        String dir = reader.readLine();

        init();
        listAllFiles(Path.of(dir), allFiles);

        List<Path> paths = allFiles
                .stream()
                .filter(file -> {
                            String fileName = file.getFileName().toString().split("/")[0];
                            return fileName.endsWith("java") || fileName.endsWith("accesswidener");
                        }
                )
                .toList();

        for (Path path : paths) {
            List<String> lines = new ArrayList<>();
            String line = mappings.remapString(Files.readString(path));
            lines.add(line);
            Files.write(path, lines, StandardCharsets.UTF_8);
        }

        System.out.println("Done!");
    }

    private static void init() {
        Path cacheDir = Paths.get("fabric\\run\\stackdeobf_mappings").toAbsolutePath();
        CachedMappings.create(cacheDir, mappingProvider).whenComplete((map, ex) -> {
            mappings = map;
        }).join();
    }

    private static void listAllFiles(Path currentPath, List<Path> allFiles)
            throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    listAllFiles(entry, allFiles);
                } else {
                    allFiles.add(entry);
                }
            }
        }
    }
}
