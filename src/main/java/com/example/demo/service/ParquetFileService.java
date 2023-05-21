package com.example.demo.service;

import com.example.demo.domain.PricingElem;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import static org.apache.parquet.hadoop.ParquetFileWriter.Mode.OVERWRITE;

@Component
@RequiredArgsConstructor
public class ParquetFileService {

    /**
     * Writes a collection of PricingElem objects to a Parquet file using Avro serialization.
     *
     * @param toLocalFile the path of the local file where the Parquet data will be written
     * @param schemaText the Avro schema in text format used for serialization
     * @param pricingElemList the collection of PricingElem objects to be written to the file
     * @param codec the compression codec to be used for the Parquet file
     * @throws UncheckedIOException if an I/O error occurs during the file writing process
     */
    public void writeCollectionToFile(@Nonnull String toLocalFile,
                                      @Nonnull String schemaText,
                                      @Nonnull Collection<PricingElem> pricingElemList,
                                      @Nonnull CompressionCodecName codec) {
        try {

            Schema schema = new Schema.Parser().parse(schemaText);
            //MessageType schema = MessageTypeParser.parseMessageType(schemaText);
            //SimpleGroupFactory groupFactory = new SimpleGroupFactory(schema);

            Configuration configuration = new Configuration();
            //FileSystem fileSystem = FileSystem.get(configuration);
            Path path = new Path(toLocalFile);


            try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                    .<GenericRecord>builder(path)
                    .withWriteMode(OVERWRITE)
                    .withSchema(schema)
                    .withCompressionCodec(codec)
                    .withConf(configuration)
                    .build()) {
                for (var pricingElem : pricingElemList) {
                    var builder = new GenericRecordBuilder(schema);
                    pricingElem.props().forEach(builder::set);
                    var genericRecord = builder.build();
                    writer.write(genericRecord);
                }
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }


    /**
     * Generates a schema based on the given column map. LIMITATION Only double and string types are supported.
     *
     * @param columns a map of column names and their corresponding classes
     * @return a JSON string representing the generated schema
     */
    @Nonnull
    public String generateSchema(@Nonnull Map<String, Class<?>> columns) {
        var cols = columns.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> {
                    //var type =  ? "double" : "string";

                    return entry.getValue() == Double.class ?

                            "{\"name\": \""  + entry.getKey() + "\", \"type\": [\"null\", \"double\"], \"default\": null}" :
                            "{\"name\": \""  + entry.getKey() + "\", \"type\": [\"null\", \"string\"], \"default\": null}";



                    //return "{\"name\": \"" + entry.getKey() + "\",\"type\" : \"" + type + "\", \"default\": null }";
                }).collect(Collectors.joining(","));

        return "{\n" +
                "   \"type\": \"record\",\n" +
                "   \"name\": \"name\",\n" +
                "   \"namespace\": \"namespace\",\n" +
                "   \"fields\": [" + cols + "   ]\n" +
                "}";
    }
}
