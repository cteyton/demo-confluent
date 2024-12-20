SELECT customer_id, account_type, cost, purchased_at
FROM purchases
LEFT JOIN customers_appending
ON purchases.customer_id = customers_appending.id;