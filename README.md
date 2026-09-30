# Cost Management System (CMS)

Spring Boot 3.3.x + Java 17 + PostgreSQL + JPA + JWT + MapStruct + Apache POI.

## Run
1. Create PostgreSQL database `cms`.
2. Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and a strong `JWT_SECRET` (32+ characters).
3. Run `mvn spring-boot:run`.
4. Login: `POST /api/auth/login` with `{ "username": "admin", "password": "admin123" }`.
5. Send `Authorization: Bearer <token>` to protected endpoints.

The first startup seeds the default ADMIN account if it does not exist.

## Excel import format
First row is treated as headers. Columns: `category | name | defaultUnitPrice | unitOfMeasure`.
Categories are created when missing; duplicate items in the same category are rejected row-by-row.

## Notes
- Monetary values use `BigDecimal`.
- Project item totals are calculated in the service layer.
- Project totals are recalculated whenever project items are changed and when a project is created/updated.
- Paid payments require `paidDate`.
- JPA uses `ddl-auto=update` for immediate local development. Use migrations such as Flyway/Liquibase for production.

## Main endpoints
- `POST /api/auth/login`
- `POST|GET /api/categories`
- `POST|GET /api/items`
- `POST /api/items/import` (multipart field: `file`)
- `POST|GET /api/projects`
- `GET|PUT|DELETE /api/projects/{id}`
- `POST|DELETE /api/projects/{projectId}/items/{itemId}` (POST uses `/{id}/items`)
- `POST|DELETE /api/projects/{projectId}/payments/{paymentId}` (POST uses `/{id}/payments`)
- `POST|DELETE /api/projects/{projectId}/deliveries/{deliveryId}` (POST uses `/{id}/deliveries`)
