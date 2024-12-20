SELECT customer_id, account_type, cost, purchased_at
FROM purchases
LEFT JOIN customers_updating
FOR SYSTEM_TIME AS OF purchases.purchased_at
ON purchases.customer_id = customers_updating.id;
