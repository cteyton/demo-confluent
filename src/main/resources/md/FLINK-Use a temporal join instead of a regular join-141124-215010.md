## Use a temporal join instead of a regular join

| Summary   | Use a temporal join instead of a regular join |
|-----------|---------------------------------------------|
| Category  | Flink                                       |
| Type      | Flink SQL                                   |
| Tags      | Correctness                                 |

### Description

Regular joins typically produce a full cross-product of all matching records. However, in streaming scenarios, this behavior is often undesirable, for example if you want to enrich an event with additional information. There is something designed for that purpose in Flink SQL, which is called a temporal join.

A temporal join joins one table with another table that is updated over time. This join is made possible by linking both tables using a time attribute, which allows the join to consider the historical changes in the table. When viewing the table at a specific point in time, the join becomes a time-versioned join.

The syntax of a temporal join is:

```sql
SELECT [column_list]
FROM table1 [AS <alias1>]
[LEFT] JOIN table2 FOR SYSTEM_TIME AS OF table1.{ rowtime } [AS <alias2>]
ON table1.column-name1 = table2.column-name1
```

Streaming data brings with it some changes in how to perform joins. In this video, David Anderson and Dan Weston talk about how and when to use temporal joins to combine your data. To learn more, check out our documentation on Apache Flink® SQL joins...

YouTube Open preview

### Positive Examples

Temporal join:

```sql
SELECT customer_id, account_type, cost, purchased_at
FROM purchases
LEFT JOIN customers_updating
FOR SYSTEM_TIME AS OF purchases.purchased_at
ON purchases.customer_id = customers_updating.id;
```

### Negative Examples

Regular join:

```sql
SELECT customer_id, account_type, cost, purchased_at
FROM purchases
LEFT JOIN customers_appending
ON purchases.customer_id = customers_appending.id;
```

## References

- [https://developer.confluent.io/courses/flink-sql/streaming-joins/](https://developer.confluent.io/courses/flink-sql/streaming-joins/)
- [https://docs.confluent.io/cloud/current/flink/reference/queries/joins.html#temporal-joins](https://docs.confluent.io/cloud/current/flink/reference/queries/joins.html#temporal-joins)
