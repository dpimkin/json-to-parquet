package com.example.demo;

import com.example.demo.domain.ResponsePayload;
import com.example.demo.domain.SomeSystemResponse;
import com.example.demo.service.JsonFileService;
import com.example.demo.service.ParquetFileService;
import com.example.demo.service.ResponseTransformationService;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.*;
import static java.util.Collections.min;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PerformanceTests {
    private static final Logger LOG = getLogger(PerformanceTests.class);

    @Autowired
    JsonFileService jsonFileService;

    @Autowired
    ParquetFileService parquetFileService;

    @Autowired
    ResponseTransformationService responseTransformationService;

    @Value("${tests.warmUpCycles:1}")
    int warmUpCycles;

    @Value("${tests.totalCycles:1}")
    int totalCycles;

    @Value("${tests.coolDown}")
    private Duration coolDown;


    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {"UNCOMPRESSED", "GZIP", "ZSTD", "SNAPPY"})
    void testParquetSerializationPerformanceOnTinyFile(String codec) throws InterruptedException {
        measureSerializationPerformance("samples/tiny.json", codec);
        //measureSerializationPerformance("samples/response_7.json", codec);
    }

    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {"UNCOMPRESSED", "GZIP", "ZSTD", "SNAPPY"})
    void testParquetSerializationPerformanceOnSmallFile(String codec) throws InterruptedException {
        measureSerializationPerformance("samples/small.json", codec);
    }

    private void measureSerializationPerformance(String sample, String codec) throws InterruptedException {
        var compression = CompressionCodecName.valueOf(codec);
        var outputParquet = sample + compression.getExtension();
        var response = jsonFileService.loadJsonFile(getResourceFilePath(sample), SomeSystemResponse.class);
        var columns = responseTransformationService.buildColumnsType(response);

        var schema = parquetFileService.generateSchema(columns);

        var pricingList = ofNullable(response.getResponsePayload())
                .map(ResponsePayload::getPricingLineList)
                .orElse(response.getPricingLineList());

        LOG.info("warming up with {}", codec);
        for (int i = 0; i <= warmUpCycles; i++) {
            parquetFileService.writeCollectionToFile(
                    outputParquet,
                    schema,
                    pricingList, compression);
        }

        LOG.info("measuring performance for {}", codec);
        var measurements = new ArrayList<Long>();
        for (int i = 0; i <= totalCycles; i++) {
            var before = System.nanoTime();
            {
                parquetFileService.writeCollectionToFile(
                        outputParquet,
                        schema,
                        pricingList, compression);

            }
            var after = System.nanoTime();

            Runtime.getRuntime().gc();
            System.gc();
            System.runFinalization();
            TimeUnit.MILLISECONDS.sleep(coolDown.toMillis());
            measurements.add(after - before);
        }



        long mean = (long) calculateMean(measurements);
        long stdDev = (long) calculateStandardDeviation(measurements);
        long p90 = calculatePercentile(measurements, 90);
        long p95 = calculatePercentile(measurements, 95);
        long p99 = calculatePercentile(measurements, 99);
        LOG.info("{} {} min = {} ms max = {} ms mean = {} ms std. dev = {} p90 = {} p95 = {} p99 = {}",
                sample, codec, NANOSECONDS.toMillis(min(measurements)), NANOSECONDS.toMillis(max(measurements)),
                NANOSECONDS.toMillis(mean), NANOSECONDS.toMillis(stdDev),
                NANOSECONDS.toMillis(p90), NANOSECONDS.toMillis(p95), NANOSECONDS.toMillis(p99));
        LOG.info("{}", measurements);
    }


    public static double calculateMean(List<Long> numbers) {
        long sum = 0;
        for (Long num : numbers) {
            sum += num;
        }
        return (double) sum / numbers.size();
    }

//    public static double calculateStandardDeviation(List<Long> numbers) {
//        double mean = calculateMean(numbers);
//        double variance = 0;
//        for (Long num : numbers) {
//            variance += Math.pow(num - mean, 2);
//        }
//        return Math.sqrt(variance / numbers.size());
//    }

    public static double calculateStandardDeviation(List<Long> numbers) {
        double mean = calculateMean(numbers);
//        // get the sum of array
//        double sum = 0.0;
//        for (double i : numbers) {
//            sum += i;
//        }
//
//        // get the mean of array
//        int length = array.length;
//        double mean = sum / length;

        // calculate the standard deviation
        double variance = 0.0;
        for (double num : numbers) {
            variance += Math.pow(num - mean, 2);
        }

        return Math.sqrt(variance / numbers.size());
    }

    public static long calculatePercentile(List<Long> numbers, int percentile) {
        sort(numbers);
        int index = (int) Math.ceil((percentile / 100.0) * numbers.size());
        return numbers.get(index - 1);
    }

    private String getResourceFilePath(String resourceName) {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        String absolutePath = file.getAbsolutePath();


        return absolutePath;

//
//        var resourceUrl = getClass().getClassLoader().getResource(resource);
//        return new File(resourceUrl.getFile()).getAbsolutePath();
    }

}
