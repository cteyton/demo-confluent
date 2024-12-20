## Avoid complex types in message key

| Summary   | Using complex types in message keys can lead to non-deterministic serialization.              |
|-----------|----------------------------------------------------------------------------------------------|
| Category  | Core Kafka                                                                                   |
| Type      | Code                                                                                         |
| Tags      | Message Key, Performance, Serialization                                                      |

## Description

It is technically possible to use complex types such as Avro arrays or maps as message keys in Kafka. However, it comes with some considerations and potential downsides:

1. **Unpredictable Partitioning**: Kafka relies on the key for partitioning messages. When you use a complex type as a key, you need to ensure that it consistently produces the same hash value for the same data. Any changes or inconsistencies in serialization can lead to unpredictable partitioning behavior.

2. **Non-deterministic Serialization**: Non-deterministic serialization can create some hiccups when it comes to ordering across different serialization formats. For instance, with Avro, JSON Schema, and Protobuf, it’s best to steer clear of using maps, arrays, and objects for keys, as they don’t ensure deterministic serialization. Additionally, schema evolution can complicate the ordering of your serialized data, potentially disrupting its integrity.

3. **Performance Impact**: Complex keys can have a higher computational overhead for hashing and can lead to slower partitioning. This is especially important to consider for high-throughput systems where performance is critical.

4. **Key Size**: Large or deeply nested keys can increase the size of metadata and cause performance issues when Kafka looks up offsets or interacts with partitions.

While it is feasible and supported to use complex keys, it is generally recommended to keep keys simple (like primitive types or straightforward objects) for better performance, easier maintainability, and consistent behavior. If you must use complex types, ensure rigorous testing and schema management practices are in place.

## Positive Examples

## Negative Examples

```java
props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
    io.confluent.kafka.serializers.KafkaAvroSerializer.class);
```

```java
props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
    io.confluent.kafka.serializers.KafkaJsonSchemaSerializer.class);
```

```java
props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
    io.confluent.kafka.serializers.KafkaProtobufSerializer.class);
```

## References

- [INTERNAL] Enablement: Confluent Schema Registry (Google Slides)

## Guidelines (Packmind)

* Detect one of the following properties 