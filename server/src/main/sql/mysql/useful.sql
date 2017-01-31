
select sum(val_invoiced) from tr_fin_txn;
+--------------------+
| sum(val_invoiced)  |
+--------------------+
| 105275687.26277012 |
+--------------------+
1 row in set (0.06 sec)

select sum(amount) from tr_fin_txn_summary;
+--------------------+
| sum(amount)        |
+--------------------+
| 105275687.26277013 |
+--------------------+
