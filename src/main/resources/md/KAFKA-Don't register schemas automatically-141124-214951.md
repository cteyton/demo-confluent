# Don't register schemas automatically

| Summary | Enabling automatic schema registration can lead to potential issues such as lack of control, schema clutter, tight coupling between producers and consumers, and increased compliance and stability risks in production environments. |
|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Category | Schema Registry                                                                                                                                                                                                                                                    |
| Type     | Configuration                                                                                                                                                                                                                                                      |
| Tags     | Governance, Data Quality                                                                                                                                                                                                                                           |

## Description

The `auto.register.schemas` property determines whether the serializer should automatically register the schema with the Schema Registry. When developing, auto-registration can speed up prototyping and ease testing by automatically registering new schemas without manual intervention. It allows schemas to evolve quickly as producers add new versions automatically.

However, enabling this feature may introduce several drawbacks:

- **Lack of Control**: Automatically registering schemas means that any producer can potentially introduce a new version. If this happens unintentionally (e.g., due to a bug), it could create backward compatibility issues or impact existing consumers.
- **Schema Pollution**: Auto-registration can lead to many unnecessary or intermediate versions being registered, cluttering your schema registry with schemas that may not be used long-term.
- **Tight Coupling**: Automatic schema updates can tightly couple the producers and consumers, potentially causing runtime issues if compatibility modes are not enforced correctly.
- **Compliance and Stability Risks**: In a production environment, you might prefer a more controlled schema evolution process to ensure compatibility requirements are met and that new schema versions are thoroughly reviewed.

Here are two scenarios where you may want to **disable** schema auto-registration, and enable `use.latest.version`:

- **Using schema references to combine multiple events in the same topic** - You can use Schema references to combine events in the same topic. Disabling schema auto-registration is essential for Avro and JSON Schema serializers. Configuration examples for using the latest schema version instead of auto-registering are in the sections on combining multiple event types in the same topic (Avro) and combining multiple event types in the same topic (JSON).
  
- **Avoiding “Schema not found” exceptions** - Subtle differences can exist between a pre-registered schema and the one used by the client with code-generated classes from the pre-registered schema via a Schema Registry aware serializer. For instance, Protobuf may generate a descriptor with the type name `.google.protobuf.Timestamp` from a fully-qualified type name like `google.protobuf.Timestamp`. Schema Registry treats these variations as different, leading to auto-registering two identical schemas if auto-registration is enabled. If disabled, it may cause a “Schema not found” error. To configure the serializer to ignore minor differences and prevent unexpected “Schema not found” exceptions, set these properties in your serializer configuration:

  ```plaintext
  auto.register.schemas=false
  use.latest.version=true
  latest.compatibility.strict=false
  ```

The `use.latest.version` sets the serializer to retrieve the latest schema version for the subject, and use that for validation and serialization, ignoring the client's schema. The assumption is that if there are any differences between client and latest registered schema, they are minor and backward compatible.

Check out this [table](#) to understand the serializer behavior based on the configurations of these three properties.

## Positive Examples

```plaintext
auto.register.schemas=false
use.latest.version=true
latest.compatibility.strict=false
```

## Negative Examples

```plaintext
auto.register.schemas=true
```

## References

- [https://docs.confluent.io/platform/current/schema-registry/fundamentals/serdes-develop/index.html#handling-differences-between-preregistered-and-client-derived-schemas](https://docs.confluent.io/platform/current/schema-registry/fundamentals/serdes-develop/index.html#handling-differences-between-preregistered-and-client-derived-schemas)
- [INTERNAL] Enablement: Confluent Schema Registry | Compile time vs run time

## Detection Guidelines

Check the presence of `auto.register.schemas=false` in the properties.
Or it is set in Java code.