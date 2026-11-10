package net.yorunina.maa.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Defers Tectonic's truncate-on-open writer until serialization has succeeded. */
public final class TectonicConfigWriter extends BufferedWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger("maa/TectonicConfig");

    private final Path destination;
    private final StringWriter buffer;
    private boolean closed;

    private TectonicConfigWriter(Path destination, StringWriter buffer) {
        super(buffer);
        this.destination = destination.toAbsolutePath();
        this.buffer = buffer;
    }

    public static BufferedWriter create(Path destination) {
        return new TectonicConfigWriter(destination, new StringWriter());
    }

    /** Only the normalization after a successful load may tolerate an IO failure. */
    public static void writeBackAfterLoad(Runnable save) {
        try {
            save.run();
        } catch (RuntimeException error) {
            if (!(error.getCause() instanceof IOException)) {
                throw error;
            }
            LOGGER.warn("Tectonic configuration was loaded, but its startup write-back failed. "
                    + "Keeping the loaded settings and existing configuration file.", error);
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        closed = true;
        super.close();
        String content = buffer.toString();
        if (content.isEmpty()) {
            // Tectonic can throw while encoding, before it writes any content.
            return;
        }

        JsonElement replacement = JsonParser.parseString(content);
        if (!replacement.isJsonObject()) {
            throw new IOException("Refusing to replace the Tectonic configuration with a non-object JSON value");
        }
        if (Files.isRegularFile(destination)) {
            try (BufferedReader reader = Files.newBufferedReader(destination)) {
                if (replacement.equals(JsonParser.parseReader(reader))) {
                    // Includes identical settings with different formatting. No
                    // truncate, rename, mtime change or mapping conflict occurs.
                    return;
                }
            } catch (JsonParseException ignored) {
                // A malformed existing file still needs a complete replacement.
            }
        }

        Path temporary = Files.createTempFile(destination.getParent(), "tectonic-", ".tmp");
        try {
            Files.writeString(temporary, content);
            try {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
