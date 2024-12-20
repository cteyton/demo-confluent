# Use KRraft instead of Zookeeper

| Summary  | Replacing ZooKeeper with KRraft in Kafka environments offers improved efficiency and reduced operational complexity |
|----------|-------------------------------------------------------------------------------------------------------------------|
| Category | Core Kafka                                                                                                         |
| Type     | Configuration                                                                                                      |
| Tags     | Performance, Resilience, Operational Efficiency                                                                    |

## Description

Apache Kafka Raft (KRraft) is the consensus protocol that was introduced in KIP-500 to remove Apache Kafka’s dependency on ZooKeeper for metadata management. This greatly simplifies Kafka’s architecture by consolidating responsibility for metadata into Kafka itself, rather than splitting it between two different systems: ZooKeeper and Kafka. KRraft mode makes use of a new quorum controller service in Kafka which replaces the previous controller and makes use of an event-based variant of the Raft consensus protocol.

Benefits include:

1. KRraft enables *right-sized* clusters, optimally sized for throughput and latency, scalable to millions of partitions.
2. Enhances stability, simplifies software, and eases monitoring and support for Kafka.
3. Implements a unified security model for the entire system.
4. Offers a unified management model for configuration, networking, and communication protocols.
5. Provides a lightweight, single-process method to [get started](https://developer.confluent.io/learn/kraft) with Kafka.
6. Ensures near-instantaneous controller failover.

To ensure a smooth transition, plan migration steps carefully, test compatibility thoroughly, and maintain a minimum of three controllers for production environments.

## Positive Examples

## Negative Examples

Having a client configuration which sets `zookeeper.connect`, eg:

```plaintext
zookeeper.connect=zookeeper:2181
```

## References

- [https://developer.confluent.io/learn/kraft](https://developer.confluent.io/learn/kraft)
- [https://docs.confluent.io/platform/current/kafka-metadata/kraft.html](https://docs.confluent.io/platform/current/kafka-metadata/kraft.html)
- [https://docs.confluent.io/platform/current/installation/migrate-zk-kraft.html#migrate-zk-kraft](https://docs.confluent.io/platform/current/installation/migrate-zk-kraft.html#migrate-zk-kraft)

# Guidelines (Packmind)

Detect the presence of `zookeeper.connect` in properties.
