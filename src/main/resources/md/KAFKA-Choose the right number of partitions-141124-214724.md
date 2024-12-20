# Choose the right number of partitions

| Summary   | Having too many partitions can cause performance and stability issues, while too few may restrict throughput and consumer parallelism |
|-----------|-------------------------------------------------------------------------------------------------------------------------------|
| Category  | Core Kafka                                                                                                                    |
| Type      | Configuration                                                                                                                 |
| Tags      | Performance, Scalability                                                                                                      |

## Description

Partitions are Kafka’s unit of parallelism—barring other factors such as consumer throughput, of course—so in order to increase your overall throughput, it would make sense to use as many partitions as possible, right? Well, not necessarily. A higher partition count may have a number of consequences in the cluster including but not limited to:

- **Increased number of file handlers, which could surpass the limit set by the underlying operating system.** When a message is produced to a topic, that message is bucketed into a specific partition. Under the hood, the data is actually appended to a log segment file (more on this in the next section) and an accompanying index file is updated. A file handler is maintained for both the data file and the index file. Thus, if you have 10 topics each with 50 partitions, there are at least 2,000 file handlers at any given time. To put that into perspective, Linux typically limits the number of file descriptors to 1,024 per process.

- **Higher chance of partition unavailability when broker failover occurs.** In an ideal, resilient cluster, every partition is replicated a number of times across other brokers in the cluster. One broker is chosen to maintain the leader replica, and all other replicas are followers. In the event that a broker fails, any leader partitions that it owns become unavailable for a certain period of time while the controller (another broker within the cluster) works to elect one of the follower replicas as the new leader.

  There are three different types of broker failover that can occur: clean, unclean, and one involving a failed controller. Thankfully, in a clean shutdown, the controller can be proactive about electing leader replicas and the downtime is relatively low. That being said, when the shutdown is unclean, the controller cannot be proactive, and all of the leader partitions on the failed broker will be unavailable at once while the controller determines the leader replicas. This becomes even worse in the event that the controller/broker goes down; not only does a new controller need to be elected, it also has to initialize itself by reading metadata for every partition within ZooKeeper. In these last two cases, the time these partitions will be unavailable is directly proportional to the number of partitions in the cluster. Higher partition counts means greater downtime.

- **Increased end-to-end latency.** Consumers are only exposed to messages on the topic once the message has been committed to all in-sync replicas. With more partitions, the bandwidth required to replicate these partitions is increased. The additional latency incurred during this step results in more time between when a producer writes a message and when a consumer can read that message.

The formula for determining the number of partitions per Kafka topic has been pretty well explored over time. When creating a new topic in your Kafka cluster, you should first think about your desired throughput (t) in MB/sec. Next, consider the producer throughput that you can achieve on a single partition (p)—this is affected by producer configurations but generally sits at roughly 10 MB/sec. Finally, you need to determine the consumer throughput (c) you will have—this is application-dependent so you'll have to measure it yourself. You should anticipate having at least max(t/p, tc) partitions for that topic. So if you have a throughput requirement of 250 MB/sec, with a producer and consumer throughput of 50 MB/sec and 25 MB/sec respectively. Then you should use at least max(250/50, 250/25) = max(5, 10) = 10 partitions for that topic.

It’s important to consider your specific requirements and test different configurations to find the optimal balance. Start with a smaller number of partitions and increase as needed, monitoring performance and resource usage.

**A word of caution:**

Although it's possible to increase the number of partitions over time, one has to be careful if messages are produced with keys. When publishing a keyed message, Kafka deterministically maps the message to a partition based on the hash of the key. This provides a guarantee that messages with the same key are always routed to the same partition. This guarantee can be important for certain applications since messages within a partition are always delivered in order to the consumer. If the number of partitions changes, such a guarantee may no longer hold. To avoid this situation, a common practice is to over-partition a bit. Basically, you determine the number of partitions based on a future target throughput, say for one or two years later. Initially, you can just have a small Kafka cluster based on your current throughput. Over time, you can add more brokers to the cluster and proportionally move a subset of the existing partitions to the new brokers (which can be done online). This way, you can keep up with the throughput growth without breaking the semantics in the application when keys are used.

## Positive Examples

---

## Negative Examples

If number of partitions for a topic is 1 or more than 30 (good default but should be a practice parameter).

### Using Terraform

```hcl
resource "kafka_topic" "my_topic" {
  name = "my-topic"
  partitions = 1
  replication_factor = 1
  config = {
    "retention.ms" = "86400000"
  }
}
```

### Using Kafka CLI

```bash
kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 1 --topic topic-A
```

### Using Confluent CLI

```bash
confluent kafka topic create --if-not-exists --partitions 1 topic-A
```

## References

- [https://www.confluent.io/blog/5-common-pitfalls-when-using-apache-kafka/](https://www.confluent.io/blog/5-common-pitfalls-when-using-apache-kafka/)
- [https://www.confluent.io/blog/how-choose-number-topics-partitions-kafka-cluster/](https://www.confluent.io/blog/how-choose-number-topics-partitions-kafka-cluster/) (still a good resource, but it’s an old article that predates KRaft and many Kafka improvements)

## Packmind Guidelines

If number of partitions for a topic is 1 or more than 30 (good default but should be a practice parameter).

We'll detect `partitions = 1` that in terraform files with the following rule:

```hcl
resource "kafka_topic" "my_topic" {
  name = "my-topic"
  partitions = 1
  replication_factor = 1
  config = {
    "retention.ms" = "86400000"
  }
}
```