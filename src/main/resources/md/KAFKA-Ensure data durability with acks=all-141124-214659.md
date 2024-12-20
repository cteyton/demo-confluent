# Ensure data durability with acks=all

| Summary   | Setting your producer `acks` to `all` ensures the leader partition waits for all in-sync replicas to acknowledge the record, guaranteeing data durability during broker failure |
|-----------|-----------------------------------------------------------------------------------------------------------------------------|
| Category  | Core Kafka                                                                                                                  |
| Type      | Configuration                                                                                                               |
| Tags      | Durability                                                                                                                  |

## Description

Setting your producer `acks` to `all` ensures the leader partition for a topic will wait for the full set of in-sync replicas to acknowledge the record, ensuring data durability in the case of a broker failure.

The number of acknowledgments the producer requires the leader to have received before considering a request complete. This controls the durability of records that are sent. The following settings are allowed:

- **acks=0** If set to zero then the producer will not wait for any acknowledgment from the server at all. The record will be immediately added to the socket buffer and considered sent. No guarantee can be made that the server has received the record in this case, and the `retries` configuration will not take effect (as the client won’t generally know of any failures). The offset given back for each record will always be set to `-1`.

- **acks=1** This will mean the leader will write the record to its local log but will respond without awaiting full acknowledgement from all followers. In this case should the leader fail immediately after acknowledging the record but before the followers have replicated it then the record will be lost.

- **acks=all** This means the leader will wait for the full set of in-sync replicas to acknowledge the record. This guarantees that the record will not be lost as long as at least one in-sync replica remains alive. This is the strongest available guarantee. This is equivalent to the acks=-1 setting.

## Positive Examples

`client.properties` file:

```plaintext
acks=all
```

## Negative Examples

`client.properties` file:

```plaintext
acks=1
```

`client.properties` file:

```plaintext
acks=0
```

## References

- [Confluent Documentation](https://docs.confluent.io/platform/current/installation/configuration/producer-configs.html)
- [Confluent Support Portal](https://support.confluent.io/hc/en-us/articles/360060854672-Confluent-Platform-Producers-and-Consumers)

## Detection Guidelines 
```
acks=0 ou 1
````
in properties file or java file