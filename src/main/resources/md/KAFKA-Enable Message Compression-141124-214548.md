# Enable Message Compression

| Summary  | Enable compression to improve throughput and efficiency by reducing network traffic and disk utilization. |         
|----------|----------------------------------------------------------------------------------------------------------|
| Category | Kafka Client                                                                                             |
| Type     | Configuration                                                                                             |
| Tags     | Performance                                                                                               |

## Description

We recommend enabling message compression for several reasons:

- **Improved throughput and efficiency**: Compression reduces the size of messages, leading to less network traffic and better disk utilization on Kafka brokers. Producers are responsible for compressing data, ideally batching data going to the same partition to maximize the benefits of compression.
- **Cost savings**: Smaller messages mean less storage is required on disk and less bandwidth is used for network transfer, which can translate into cost savings, especially in cloud environments.
- **Better performance**: While compression and decompression introduce some CPU overhead, the reduction in network and disk I/O typically results in a net performance boost—especially when dealing with large messages or high-throughput workloads.

Kafka offers multiple compression algorithms via the `compression.type` property, each with distinct performance profiles:

- **lz4**: Strikes a balance between compression speed and ratio, making it a common recommendation.
- **gzip and zstd**: Provide higher compression ratios but are more CPU-intensive.
- **snappy**: Offers faster compression at the cost of lower compression ratios.

Performance can vary depending on your specific data and workloads, so testing different algorithms to find the best fit for your needs is recommended.

It's important to note that compressed data must be decompressed by consumers, which adds some CPU overhead on the consumer side. However, the performance benefits from reduced network traffic and disk I/O usually outweigh the decompression cost. Consumers automatically handle decompression based on a header in the compressed message.

**Consumer Considerations**: Note that consumers must decompress messages, which introduces some CPU overhead. However, the performance gains from reduced network traffic and disk I/O often outweigh this cost, with consumers automatically managing decompression via headers in compressed messages.

**Configuration Recommendations**: We advise configuring compression at the producer level using the `compression.type` property. Configuring compression at the topic or broker level can introduce inefficiencies if there are mismatches in compression settings. For example, if a broker’s compression setting differs from the producer’s, the broker may need to decompress and re-compress messages, adding unnecessary overhead.

## Positive examples

Kafka client configuration:

```plaintext
compression.type=lz4
```

```plaintext
compression.type=snappy
```

```plaintext
compression.type=zstd
```

```plaintext
compression.type=gzip
```

## Negative examples

Kafka client configuration:

```plaintext
# compression.type isn't set
```

```plaintext
compression.type=none
```

References

- [https://www.confluent.io/blog/apache-kafka-message-compression/](https://www.confluent.io/blog/apache-kafka-message-compression/)

## Detection Guidelines

Check missing compression.type configuration in the Kafka client configuration file.
Or in Java code.
