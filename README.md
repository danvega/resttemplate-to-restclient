# RestTemplate to RestClient

Spring Framework 7.1 deprecates `RestTemplate` for removal, and Spring Boot 4.2 deprecates its `RestTemplate` support with it. This repo is a small package tracker that calls a carrier tracking API. It uses the `RestTemplate` features most apps use, and walks through moving each one to `RestClient`.

This is the `start` branch. It runs on Spring Boot 4.1 with `RestTemplate`, and every test passes. The `main` branch holds the finished migration.

## Tested with

Java 25, Spring Boot 4.1.3.

## What you need

- JDK 25 or newer.

## Run it

The tracker calls a fake carrier API, so start that first. It runs on port 8081.

```bash
cd carrier-api && ./mvnw spring-boot:run
```

Then start the package tracker in a second terminal. It runs on port 8080.

```bash
./mvnw spring-boot:run
```

## Try it

List every shipment the carrier knows about (four of them):

```bash
curl localhost:8080/api/shipments
```

Get one shipment:

```bash
curl localhost:8080/api/shipments/1Z999AA10123456785
```

Ask for one that does not exist. The carrier returns a 404, and the tracker turns it into a problem detail:

```bash
curl -i localhost:8080/api/shipments/NOPE
```

## Where to look

- [CarrierClientConfig.java](src/main/java/dev/danvega/tracker/carrier/CarrierClientConfig.java) builds the `RestTemplate` with a root URI, timeouts, and an error handler.
- [ApiKeyCustomizer.java](src/main/java/dev/danvega/tracker/carrier/ApiKeyCustomizer.java) adds the `X-Api-Key` header with a `RestTemplateCustomizer`.
- [CarrierClient.java](src/main/java/dev/danvega/tracker/carrier/CarrierClient.java) makes the calls with `getForObject` and `exchange`.
- [CarrierErrorHandler.java](src/main/java/dev/danvega/tracker/carrier/CarrierErrorHandler.java) turns a 404 into `ShipmentNotFoundException`.
- [CarrierClientTest.java](src/test/java/dev/danvega/tracker/carrier/CarrierClientTest.java) uses `@RestClientTest` and `MockRestServiceServer`.
- [ShipmentControllerTest.java](src/test/java/dev/danvega/tracker/ShipmentControllerTest.java) uses `TestRestTemplate`.

## The migration

1. In `pom.xml`, change the Spring Boot version to `4.2.0`.
2. Run `./mvnw verify`. The tests still pass, but the compiler prints ten "deprecated and marked for removal" warnings.
3. Swap the `RestTemplateBuilder` bean for `RestClient.Builder`. `rootUri` becomes `baseUrl`. The timeouts move to `spring.http.clients.connect-timeout` and `spring.http.clients.read-timeout`.
4. Swap `RestTemplateCustomizer` for `RestClientCustomizer`. The interceptor becomes `defaultHeader`.
5. Rewrite the calls. `getForObject` becomes `get().uri(...).retrieve().body(...)`. `exchange` with a `ParameterizedTypeReference` becomes `body(new ParameterizedTypeReference<>() {})`.
6. Delete `CarrierErrorHandler`. One `onStatus` call on the request does the same job.
7. Keep `@RestClientTest`. It works with `RestClient.Builder`. Switch `TestRestTemplate` to `RestTestClient` with `@AutoConfigureRestTestClient`.
8. Run `./mvnw verify` again. No warnings, all tests green.

## Branches

| Branch | What it has |
| --- | --- |
| `start` | Spring Boot 4.1.3 with `RestTemplate`. Start here. |
| `step-1-boot-4.2` | Spring Boot 4.2.0. Ten removal warnings, tests still pass. |
| `step-2-restclient-bean` | A `RestClient` bean built with `baseUrl`. |
| `step-3-timeouts-yaml` | The timeouts move to `spring.http.clients` in `application.yaml`. |
| `step-4-customizer` | `RestClientCustomizer` with `defaultHeader` replaces the interceptor. |
| `step-5-client-calls` | The calls use `RestClient`, with `onStatus` for the 404. |
| `step-6-delete-error-handler` | `CarrierErrorHandler` is gone. |
| `step-7-full-url-tests` | Full URLs in the `MockRestServiceServer` expectations. |
| `step-8-rest-test-client` | `RestTestClient` replaces `TestRestTemplate`. Same code as `main`. |
| `main` | Spring Boot 4.2.0 with `RestClient`. The finished code. |

Each `step-` branch adds one change on top of the one before. To see a single change, compare a step with the branch before it. Some tests fail in steps 2 to 6 because the app is only half migrated there. Step 7 makes them pass again.

## Good to know

- `carrier-api` is a stand-in for a real carrier. It never changes during the migration. It expects the header `X-Api-Key: demo-key`.
- With `rootUri`, Boot's `MockRestServiceServer` matches relative paths like `/shipments`. That trick only works for `RestTemplate`. After the move to `RestClient`, the test expectations need the full URL.
