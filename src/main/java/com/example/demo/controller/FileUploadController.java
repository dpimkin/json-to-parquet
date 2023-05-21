package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Paths;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileUploadController {

    @Value("${storage.input}")
    String input;

    /**
     * Uploads a file to the server.
     *
     * @param filePartMono The file part to be uploaded.
     * @return A Mono with a string indicating the status of the upload.
     */
    @PostMapping(value = "/api/v1/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadFile(@RequestPart("file") Mono<FilePart> filePartMono) throws IOException {
       log.info("cwd {}", System.getProperty("user.dir"));

        return filePartMono.flatMap(filePart -> filePart.transferTo(Paths.get(System.getProperty("user.dir"),
                        input, filePart.filename()).toFile()))
                .then(Mono.just("File uploaded successfully."));
    }
}
