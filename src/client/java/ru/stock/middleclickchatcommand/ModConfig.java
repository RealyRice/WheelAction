package ru.stock.middleclickchatcommand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String DEFAULT_COMMAND_TEMPLATE = "/duel {name}";
    private static final Path PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("middleclick-chat-command.json");

    public static Data data = new Data();

    private ModConfig() {}

    public static void load() {
        if (!Files.exists(PATH)) {
            data.normalize();
            save();
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
            Data loaded = GSON.fromJson(reader, Data.class);
            if (loaded != null) {
                loaded.normalize();
                data = loaded;
            } else {
                data = new Data();
            }
        } catch (Exception ignored) {
            data = new Data();
        }
        data.normalize();
    }

    public static void save() {
        try {
            data.normalize();
            Files.createDirectories(PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception ignored) {
        }
    }

    public static final class Data {
        /**
         * Template command executed on middle click.
         * Use {name} placeholder for player name.
         */
        public String commandTemplate = DEFAULT_COMMAND_TEMPLATE;

        /**
         * If true, will try to infer a name even if chat component has no insertion/click metadata.
         */
        public boolean fallbackWordDetection = true;

        public void normalize() {
            if (commandTemplate == null || commandTemplate.isBlank()) {
                commandTemplate = DEFAULT_COMMAND_TEMPLATE;
            } else {
                commandTemplate = commandTemplate
                        .replace("{player}", "{name}")
                        .replace("{nick}", "{name}");
            }
        }
    }
}

