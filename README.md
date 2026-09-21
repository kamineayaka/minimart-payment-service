# minimart-payment-service

MiniMart 的支付进程（Payment 收钱记录）。领域用语与 v1 契约在编排仓 [`minimart-infra`](../minimart-infra)。Order 状态机仍在 `minimart-order-service`。不上 Seata。

- Spring 名：`payment-service`
- 端口：8084
- 库：`minimart_payment`（由 infra 的 `docker/mysql/init.sql` 建；本进程尚未连库）
- Feign：成功/失败后通知 order 的幂等 pay-result。不写 Order 或 Stock。
- Feign 目标 URL 默认 `http://order-service:8083`（K8s Service DNS），见 `application.yaml`。

本机运行：

```bash
./gradlew bootRun
```

编排：与其它仓并列 clone 后，在 `minimart-infra` 执行 `docker compose up`（无 Nacos；Docker DNS 与 K8s Service 名一致）。
