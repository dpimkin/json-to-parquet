package com.example.demo.controller;

import com.example.demo.domain.PricingElem;
import com.example.demo.domain.ResponsePayload;
import com.example.demo.service.JsonFileService;
import com.example.demo.service.ParquetFileService;
import com.example.demo.domain.SomeSystemResponse;
import com.example.demo.service.ResponseTransformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;

import static java.util.Optional.ofNullable;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConvertFileController {
    private final JsonFileService jsonFileService;
    private final ParquetFileService parquetFileService;
    private final ResponseTransformationService responseTransformationService;


    @Value("${storage.input}")
    String input;

    @Value("${storage.output}")
    String output;


    @PostMapping(value = "api/v1/convert/{compression:UNCOMPRESSED|GZIP|ZSTD|SNAPPY}/{fileName}")
    @RegisterReflectionForBinding({PricingElem.class, ResponsePayload.class, SomeSystemResponse.class, Configuration.class})
    Mono<? extends ResponseEntity<?>> convert(@PathVariable("compression") String compression,
                                              @PathVariable("fileName") String inputJsonFile) {
        var codec = CompressionCodecName.valueOf(compression);
        var inputFilePath =  Paths.get(System.getProperty("user.dir"), input, inputJsonFile);
        if (!inputFilePath.toFile().exists()) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        var outputParquet = Paths.get(System.getProperty("user.dir"), output,
                inputJsonFile + codec.getExtension()).toFile().getAbsolutePath();

        log.info("input {} output {}", inputFilePath, outputParquet);

        return Mono.fromCallable(() -> {
            var initial = System.nanoTime();
            var response = jsonFileService.loadJsonFile(inputFilePath.toFile(), SomeSystemResponse.class);

            var columns = responseTransformationService.buildColumnsType(response);
            var before = System.nanoTime();

            var pricingList = ofNullable(response.getResponsePayload())
                    .map(ResponsePayload::getPricingLineList)
                    .orElse(response.getPricingLineList());

            var schema = parquetFileService.generateSchema(columns);
            parquetFileService.writeCollectionToFile(
                    outputParquet,
                    schema,
                    pricingList, codec);
            var after = System.nanoTime();
            return ResponseEntity.ok(" parsing json " + (before - initial) + " persisting to parquet " + (after - before) + '\n');
        });
    }
}
