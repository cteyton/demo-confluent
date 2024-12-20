 # Avoid setting `request.timeout.ms` too low

| Summary | Setting request.timeout.ms to a low value can lead to increased pressure on the broker, especially if it is already slow in processing requests. |
| --- | --- |
| Category | Core Kafka |
| Type | Configuration |
| Tags | Performance |

## Description

`request.timeout.ms` is a client-side configuration that defines how long the client (both producer and consumer) will wait to receive a response from the broker. The default for this configuration is set to **30 seconds**.

If a response is not received before the timeout is reached, then one of two things will happen: either the client will attempt to resend the request (if retries are enabled and have not yet been exhausted, see below for more details on this) or it will simply fail.

It might be tempting to set the `request.timeout.ms` to a lower value. After all, with a shorter timeout period, clients can react more quickly, whether that means reconnecting or even failing. However, whilst this might sound intuitive, it’s not always a good thing. If you’re not careful, you might exacerbate any problems on the broker side and result in worse performance for your application.

For example, if a broker is taking a long time to handle and process its incoming requests, a lower request.timeout.ms across client applications could lead to increased request pressure as the additional retry attempts are added to the broker’s request queue. This then exacerbates the ongoing performance impact on the brokers, adding to the pressure on it.

It’s recommended to leave `request.timeout.ms` at the default value of 30 seconds. As discussed above, a lower value could actually increase the amount of time it takes for server-side requests to be processed. Depending on your application needs and if you’re seeing these timeouts often, it may actually be useful to set request.timeout.ms to a higher value.

## Positive Examples

Kafka client configuration file:

```plaintext
# request.timeout.ms not being set (defaults to 30)
request.timeout.ms=30
```

## Negative Examples

Setting `request.timeout.ms` to a low value (< 20)

```plaintext
request.timeout.ms=0
```

```plaintext
request.timeout.ms=5
```

```plaintext
request.timeout.ms=10
```

```plaintext
request.timeout.ms=15
```

## References

- [https://www.confluent.io/blog/5-common-pitfalls-when-using-apache-kafka/](https://www.confluent.io/blog/5-common-pitfalls-when-using-apache-kafka/)

## Detection Guidelines (Packmind)

* Detect if the properties `request.timeout.ms` in set under 30 secondes (at most 29).
