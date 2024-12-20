
 ## Group by window start and end when using table-valued functions

| Summary  | Group by window start and end when using table-valued functions      |
|----------|---------------------------------------------------------------------|
| Category | Flink                                                               |
| Type     | Flink SQL                                                           |
| Tags     | Correctness, Table Values Functions                                |

### Description

The tables returned by [table-valued functions](#) are designed to have their output rows rolled up by aggregating together all of the rows with the same `window_start` and `window_end` values.

Unlike HOP, CUMULATE, and SESSION windows, TUMBLE windows can be uniquely identified by their `window_start` (or `window_end`), so it seems unnecessary to bother with grouping by both of these columns in the case of a tumble window. However, it does make a big difference.

It's worth doing the experiment. The two queries below differ only in the `GROUP BY` clause. Try them both, and use the "Show changelog" option triggered by typing an M in the CLI to see in more detail what’s happening with each version:

```sql
WITH one_thousand_orders AS (
  SELECT *, $rowtime FROM 'examples'.'marketplace'.'orders' LIMIT 1000
)
SELECT window_start, COUNT(*) AS order_count
FROM TABLE(
  TUMBLE(TABLE one_thousand_orders,
    DESCRIPTOR($rowtime),
    INTERVAL '5' SECOND))
GROUP BY window_start, window_end;
```

```sql
WITH one_thousand_orders AS (
  SELECT *, $rowtime FROM 'examples'.'marketplace'.'orders' LIMIT 1000
)
SELECT window_start, COUNT(*) AS order_count
FROM TABLE(
  TUMBLE(TABLE => TABLE one_thousand_orders,
    TIMECOL => DESCRIPTOR($rowtime),
    SIZE => INTERVAL '5' SECOND))
GROUP BY window_start;
```

The Flink SQL planner recognizes the version of this query with `GROUP BY window_start, window_end` as a windowing query, and executes it with a special window operator. This means that it produces an insert-only table as its result, and it clears from the state backend all information about each window as soon as it emits its result.

On the other hand, the query that specifies `GROUP BY window_start` is executed as a normal aggregation.

### Positive Examples

```sql
WITH one_thousand_orders AS (
  SELECT *, $rowtime FROM 'examples'.'marketplace'.'orders' LIMIT 1000
)
SELECT window_start, COUNT(*) AS order_count
FROM TABLE(
  TUMBLE(DATA => TABLE one_thousand_orders,
```
 Sure! Here’s the extracted content formatted in Markdown:

```sql
TIMECOL => DESCRIPTOR($rowtime),
SIZE => INTERVAL '5' SECOND))
GROUP BY window_start, window_end;
```

### Negative Examples

```sql
WITH one_thousand_orders AS
    (SELECT *, $rowtime FROM `examples`.`marketplace`.`orders` LIMIT 1000)
SELECT window_start, COUNT(*) AS order_count
FROM TABLE(
    TUMBLE(TABLE one_thousand_orders,
        TIMECOL => DESCRIPTOR($rowtime),
        SIZE => INTERVAL '5' SECOND))
GROUP BY window_start;
```

### References

- [https://developer.confluent.io/courses/flink-sql/streaming-analytics-exercise/](https://developer.confluent.io/courses/flink-sql/streaming-analytics-exercise/)
- [https://docs.confluent.io/cloud/current/flink/reference/queries/window-tvf.html](https://docs.confluent.io/cloud/current/flink/reference/queries/window-tvf.html)