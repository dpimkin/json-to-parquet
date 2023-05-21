package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonFileService {

    private final ObjectMapper objectMapper;

    @Nonnull
    public <T> T loadJsonFile(@Nonnull String fromLocalFile, @Nonnull Class<T> DtoClass) {
        try {
            var file = Paths.get(requireNonNull(fromLocalFile)).toFile();

            if (!file.exists()) {
                log.info("file not found at {}", file.getAbsoluteFile());
                throw new IllegalArgumentException("File " + fromLocalFile + " was not found");
            }

            return objectMapper.readValue(file, requireNonNull(DtoClass));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Nonnull
    public <T> T loadJsonFile(@Nonnull File fromLocalFile, @Nonnull Class<T> DtoClass) {
        try {
            if (!fromLocalFile.exists()) {
                log.info("file not found at {}", fromLocalFile.getAbsoluteFile());
                throw new IllegalArgumentException("File " + fromLocalFile + " was not found");
            }

            return objectMapper.readValue(fromLocalFile, requireNonNull(DtoClass));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> void saveJsonFile(@Nonnull String toLocalFile, @Nonnull T data) {
        try {
            objectMapper.writeValue(Paths.get(requireNonNull(toLocalFile)).toFile(), requireNonNull(data));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
