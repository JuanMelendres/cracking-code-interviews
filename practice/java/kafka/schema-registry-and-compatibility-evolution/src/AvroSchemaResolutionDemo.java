import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * What the Schema Registry's compatibility check is actually protecting: real Avro
 * writer/reader schema resolution. Encodes one real record with the v1 (writer)
 * schema, then decodes those exact bytes using a DIFFERENT reader schema — proving
 * directly, at the byte level, why "add a field with a default" is safe (the decoder
 * fills the default in) and why "add a field with no default" genuinely cannot be
 * made safe (there is nothing for the decoder to fill in).
 */
public class AvroSchemaResolutionDemo {
    public static void main(String[] args) throws Exception {
        Schema v1 = new Schema.Parser().parse(Files.readString(Path.of("schemas/order-v1.avsc")));
        Schema v2 = new Schema.Parser().parse(Files.readString(Path.of("schemas/order-v2-add-with-default.avsc")));
        Schema v3 = new Schema.Parser().parse(Files.readString(Path.of("schemas/order-v3-add-no-default.avsc")));

        GenericRecord record = new GenericData.Record(v1);
        record.put("orderId", "order-123");
        record.put("customerId", "cust-42");
        record.put("amount", 59.99);

        byte[] bytes = encode(record, v1);
        System.out.println("Real bytes written with the v1 (writer) schema: " + bytes.length + " bytes");
        System.out.println("(no 'currency' field exists anywhere in these bytes — v1 never had one)");

        System.out.println();
        System.out.println("=== Decoding those exact v1 bytes with the v2 reader schema (adds 'currency', default \"USD\") ===");
        GenericRecord decodedAsV2 = decode(bytes, v1, v2);
        System.out.println("Real decoded record: " + decodedAsV2);
        System.out.println("currency = " + decodedAsV2.get("currency") + "  <- filled in from the schema's default, not from the bytes");

        System.out.println();
        System.out.println("=== Decoding those exact v1 bytes with the v3 reader schema (adds 'shippingAddress', NO default) ===");
        try {
            GenericRecord decodedAsV3 = decode(bytes, v1, v3);
            System.out.println("UNEXPECTED: decode succeeded: " + decodedAsV3);
        } catch (Exception e) {
            System.out.println("Real failure, as expected: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println("There is no default to fall back to and no bytes on the wire for this field —");
            System.out.println("resolution has no correct value to produce. This is the exact defect the Schema");
            System.out.println("Registry's real HTTP 409 in registry-demo.sh prevents from ever reaching production.");
        }
    }

    private static byte[] encode(GenericRecord record, Schema writerSchema) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        new GenericDatumWriter<GenericRecord>(writerSchema).write(record, encoder);
        encoder.flush();
        return out.toByteArray();
    }

    private static GenericRecord decode(byte[] bytes, Schema writerSchema, Schema readerSchema) throws Exception {
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(new ByteArrayInputStream(bytes), null);
        DatumReader<GenericRecord> reader = new GenericDatumReader<>(writerSchema, readerSchema);
        return reader.read(null, decoder);
    }
}
