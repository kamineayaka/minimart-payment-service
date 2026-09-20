# minimart-payment-service

MiniMart 的支付进程（Payment 收钱记录）。领域用语与 v1 契约在编排仓 [`minimart-infra`](../minimart-infra)。Order 状态机仍在 `minimart-order-service`。不上 Seata。

- Spring 名：`payment-service`
- 端口：8084
- 库：`minimart_payment`（由 infra 的 `docker/mysql/init.sql` 建）

本机运行（Nacos 需已起，`NACOS_ADDR=127.0.0.1:8848`）：

```bash
./gradlew bootRun
```

编排：与其它仓并列 clone 后，在 `minimart-infra` 执行 `docker compose up`。
