# Use schemas to ensure data consistency and compatibility

| Summary |  |
|---------|--|
| **Category** | Topics and Data Management |
| **Type** | Code |
| **Tags** | Governance, Data Management |

## Description

When working with Apache Kafka as a distributed event streaming platform, one of the most critical recommendations for maintaining data integrity and system scalability is to use schemas for data consistency and compatibility. A schema, essentially, acts as a blueprint defining the structure of the data a Kafka topic handles, including field names, data types, and potential constraints (more on this in the last section of this practice). By adopting schemas, you enforce a well-defined contract between producers that write data and consumers that read it, making your systems more predictable and resilient.

Without schemas, producers and consumers can end up exchanging data with inconsistencies—such as missing fields, unexpected data types, or changes in the structure—leading to potential deserialization errors, misinterpretations of data, and brittle integrations. Consider a simple example: A producer application writes user data to a Kafka topic, including fields like `user_id`, `name`, and `age`. If the producer is modified to remove the `name` field or change `age` from an integer to a string without notifying consumers, downstream applications expecting the original data structure can break.

To avoid this, schemas should be defined using a serialization format such as **Avro**, **Protobuf**, or **JSON Schema**, and stored in a schema registry. The **Confluent Schema Registry**, for instance, provides centralized schema management, versioning, and validation features. With this setup, producers can validate data before publishing it, while consumers retrieve and use the correct schema to deserialize messages.

Using schemas offers a multitude of advantages:

1. **Data Validation**: Producers validate messages against a schema before publishing. Invalid data is rejected immediately, reducing downstream issues.

2. **Compatibility Rules**: You can enforce compatibility rules such as `BACKWARD`, `FORWARD`, or `FULL`, to ensure evolving schemas don't disrupt existing consumers.

3. **Clear Communication**: By using schemas, different teams or services consuming data from Kafka topics can quickly understand and integrate with the data format without ambiguity or fragile custom parsing logic.

4. **Streamlined Serialization**: Formats like Avro are efficient and compact, reducing message size and improving performance while maintaining rich data structures.

If you’re using Confluent Cloud or Confluent Platform, consider enabling broker-side schema validation to further enforce the usage of schemas across your data streaming platform.

## Example: Evolution and Compatibility

Suppose your producer initially defined a schema for user events like this (using Avro as an example):

```json
{
    "type": "record",
    "name": "User",
    "fields": [
        {"name": "user_id", "type": "string"},
        {
          "name": "name", "type": "string"
        },
        {
          "name": "age", "type": "int"
        } 
    ]
}
```

Later, you want to add an `email` field. By registering the updated schema with compatibility rules like FORWARD compatibility, the new schema allows new data to include `email` while ensuring older consumers continue to function as they will still receive the expected fields (`user_id`, `name`, `age`) and disregard the new one.

Updated schema:

```json
{
  "type": "record",
  "name": "User",
  "fields": [
    {"name": "user_id", "type": "string"},
    {"name": "name", "type": "string"},
    {"name": "age", "type": "int"},
    {"name": "email", "type": "string"}
  ]
}
```

### Do more with Confluent’s Data Contracts rules

- Confluent Schema Registry supports declarative schema migration rules (available with Confluent Cloud and Confluent Platform). These rules allow transforming data between different schema versions within the same topic (intra-topic migration). This means producers can use different, incompatible schema versions, and consumers will receive data transformed into the version they expect. This simplifies schema evolution and avoids the need for inter-topic migration, which requires complex coordination during consumer switchover. Check out this video to know more: [How to Evolve your Schemas with Migration Rules | Data Quality Rules](#) (YouTube video)

- Confluent Schema Registry also supports declarative Domain Validation Rules to enforce data quality right before it enters your data streaming platform. Domain Validation Rules act as filters for your data streams, enforcing specific criteria to ensure only valid data gets through (for example, a valid social security number or a valid email address, or a combination of several fields). This keeps your data clean at the source, cutting down on manual cleaning and minimizing downstream issues. By catching and blocking invalid data upfront, you maintain data integrity and reliability, resulting in accurate analytics and efficient pipelines. It helps you focus on building robust solutions without worrying about data inconsistencies slowing you down. Check out this video to know more: [How To Improve Data Quality with Domain Validation Rules | Data Quality Rules](#)

### Positive Examples

`Payment.avsc` (with the `Payment` Java class being generated from this Avro definition):

```json
{
  "namespace": "io.confluent.examples.clients.basicavro",
  "type": "record",
  "name": "Payment",
  "fields": [
    {"name": "id", "type": "string"},
    {"name": "amount", "type": "double"}
  ]
}
```

`ProducerExample.java`:

```java
public class ProducerExample {
  private static final String TOPIC = "payments";
  
  public static void main(final String[] args) throws IOException {
    // Configure the Producer
    final Properties props = new Properties();
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

    try (Producer<String, Payment> producer = new KafkaProducer<String, Payment>(props)) {

        for (long i = 0; i < 10; i++) {
            final Payment payment = new Payment("id-" + Long.toString(i), 1000.00d);
            final ProducerRecord<String, Payment> record = new ProducerRecord<String, Payment>(TOPIC, 
            payment.getId(), payment);
            producer.send(record);
            Thread.sleep(1000L);
        }

        producer.flush();
        System.out.printf("Successfully produced 10 messages to a topic called %s%n", TOPIC);
    } catch (final SerializationException e) {
        e.printStackTrace();
    } catch (final InterruptedException e) {
        e.printStackTrace();
    }
}
```

### Negative Examples

`Payment.java` (or an equivalent java record class):

```java
class Payment {
    private String id;
    private double amount;

    // Constructor
    public Payment(String id, double amount) {
        this.id = id;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
```

`ProducerExample.java` (or an equivalent java record class):

```java
public class ProducerExample {

    private static final String TOPIC = "payments";

    public static void main(final String[] args) throws Exception {
        // Configure the Producer
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create the producer
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {

            // Create ObjectMapper for JSON conversion
            ObjectMapper objectMapper = new ObjectMapper();

            // Produce messages in a loop
            for (int i = 0; i < 10; i++) {

                Payment payment = new Payment("id-" + Long.toString(i), 1000.00d);

                // Convert the Payment object to a JSON string
                String paymentJson = objectMapper.writeValueAsString(payment);

                ProducerRecord<String, Payment> record = new ProducerRecord<>(TOPIC, payment.getId(), paymentJson);
                producer.send(record);
                Thread.sleep(1000L);
            }
            producer.flush();
            System.out.printf("Successfully produced 10 messages to a topic called %s%n", TOPIC);

        } catch (final SerializationException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### References

- [https://docs.confluent.io/cloud/current/sr/fundamentals/data-contracts.html](https://docs.confluent.io/cloud/current/sr/fundamentals/data-contracts.html)
- [https://www.confluent.io/blog/shift-left-bad-data-in-event-streams-part-1/](https://www.confluent.io/blog/shift-left-bad-data-in-event-streams-part-1/)
- [https://www.confluent.io/blog/shift-left-bad-data-in-event-streams-part-2/](https://www.confluent.io/blog/shift-left-bad-data-in-event-streams-part-2/)
- [https://docs.confluent.io/platform/current/schema-registry/fundamentals/schema-evolution.html](https://docs.confluent.io/platform/current/schema-registry/fundamentals/schema-evolution.html)
- [https://www.confluent.io/blog/data-contracts-confluent-schema-registry/](https://www.confluent.io/blog/data-contracts-confluent-schema-registry/)
- [https://docs.confluent.io/cloud/current/sr/broker-side-schema-validation.html](https://docs.confluent.io/cloud/current/sr/broker-side-schema-validation.html)
- [https://docs.confluent.io/platform/current/schema-registry/schema-validation.html](https://docs.confluent.io/platform/current/schema-registry/schema-validation.html)

### Detection Guidelines

-> Dans l'exemple positif, comment charger le fichier `.avsc` et `ProducerExample.java` depuis `ProducerExample.java` ?


Bad:  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
Good: props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
Is that correct? 