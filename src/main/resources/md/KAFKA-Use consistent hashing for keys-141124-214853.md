## Use consistent hashing for keys

| Summary  | Use consistent hashing when using non-java producers. |
|----------|-------------------------------------------------------|
| Category | Core Kafka                                            |
| Type     | Code                                                  |
| Tags     | Correctness                                           |

### Description

When working with Kafka producers across languages, inconsistent data partitioning due to different hashing algorithms (like MurMur2 for Java and `CRC32` for non-Java clients) can create message-order issues. This results in data with the same keys landing in different partitions.

In Apache Kafka®’s Java producer library, the default partitioning strategy utilizes a hashing function to assign messages to the appropriate topic partition. The algorithm currently in use (as of Kafka 3.9) is known as `murmur2`. This name originates from its inner loop, which relies on two fundamental operations: multiplication and rotation.

Non-Java Kafka clients may employ a different hashing algorithm. For instance, the default algorithm used by librdkafka is `consistent_random`, which is based on CRC32 and does not align with Java’s MurMur2. To ensure compatibility with Java’s algorithm, you should configure librdkafka’s `partitioner` property to `murmur2_random` (not `murmur2`).

Here’s the list of possible values for the `partitioner` property in [librdkafka](https://github.com/edenhill/librdkafka):

- `random`: random distribution
- `consistent`: CRC32 hash of key (Empty and NULL keys are mapped to a single partition)
- `consistent_random`: CRC32 hash of key (Empty and NULL keys are randomly partitioned)
- `murmur2`: Java producer compatible Murmur2 hash of key (NULL keys are mapped to a single partition)
- `murmur2_random`: Java producer compatible Murmur2 hash of key (NULL keys are randomly partitioned; this is functionally equivalent to the default partitioner in the Java producer)
- `fnv1a`: FNV-1a hash of key (NULL keys are mapped to single partition)
- `fnv1a_random`: FNV-1a hash of key (NULL keys are randomly partitioned)

If you cannot (or don’t want) to change the hashing algorithm, another option is to repartition the original topic and dump it to another topic, for example using [Flink](https://flink.apache.org):

```sql
CREATE TABLE DEMO_USER_REPARTITION (
    user_id BIGINT,
    name STRING,
    age INT
)
AS
SELECT user_id, name, age
FROM DEMO_USER;
```

### Positive Examples

Client configuration of a project using any librdkafka-based client ([confluent-kafka-go](https://github.com/confluentinc/confluent-kafka-go), [confluent-kafka-dotnet](https://github.com/confluentinc/confluent-kafka-dotnet), [confluent-kafka-javascript](https://github.com/confluentinc/confluent-kafka-js), [confluent-kafka-python](https://github.com/confluentinc/confluent-kafka-python)) :

```json
{
  "bootstrap.servers": "...",
  "partitioner": "murmur2_random",
  ...
}
```

## Negative Examples

Client configuration of a project using any librdkafka-based client ([confluent-kafka-go](https://github.com/confluentinc/confluent-kafka-go), [confluent-kafka-dotnet](https://github.com/confluentinc/confluent-kafka-dotnet), [confluent-kafka-javascript](https://github.com/confluentinc/confluent-kafka-javascript), [confluent-kafka-python](https://github.com/confluentinc/confluent-kafka-python)):

```json
{
  "bootstrap.servers": "...",
  ...
}
```

```json
{
  "bootstrap.servers": "...",
  "partitioner": "consistent_random"
  ...
}
```

```json
{
  "bootstrap.servers": "...",
  "partitioner": "random"
  ...
}
```

```json
{
  "bootstrap.servers": "...",
  "partitioner": "consistent"
  ...
}
```

```json
{
  "bootstrap.servers": "...",
  "partitioner": "murmur2"
  ...
}
```

```json
{
  "bootstrap.servers": "...",
  "partitioner": "fnv1a"
  ...
}
```

```json
{
  "bootstrap.servers": "...",
  "partitioner": "fnv1a_random"
  ...
}
```

## References

- [https://www.confluent.io/blog/standardized-hashing-across-java-and-non-java-producers/](https://www.confluent.io/blog/standardized-hashing-across-java-and-non-java-producers/)
- [https://bbejeck.medium.com/a-critical-detail-about-kafka-partitioners-8e17dfd45a7](https://bbejeck.medium.com/a-critical-detail-about-kafka-partitioners-8e17dfd45a7)

# Guidelines (Packmind)

- Detect in such configuration either:
- 1/ A missing `partitioner` property and suggest adding it.
- 2/ A `partitioner` property with a value different from `murmur2_random`.