INSERT INTO store_category (id, code, description)
SELECT 1, 'food', 'food'
WHERE NOT EXISTS (SELECT 1 FROM store_category);