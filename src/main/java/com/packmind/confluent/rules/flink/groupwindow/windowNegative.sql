WITH one_thousand_orders AS
    (SELECT *, $rowtime FROM `examples`.`marketplace`.`orders` LIMIT 1000)
SELECT window_start, COUNT(*) AS order_count
FROM TABLE(
    TUMBLE(TABLE one_thousand_orders,
        TIMECOL => DESCRIPTOR($rowtime),
        SIZE => INTERVAL '5' SECOND))
GROUP BY window_start;