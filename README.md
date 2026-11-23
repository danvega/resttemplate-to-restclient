# RestTemplate to RestClient

Spring Framework 7.1 deprecates `RestTemplate` for removal, and Spring Boot 4.2 deprecates its `RestTemplate` support with it. This repo is a small package tracker that calls a carrier tracking API. It started on `RestTemplate` and now uses `RestClient`, with zero deprecation warnings and the same tests passing.

The `main` branch holds the finished code. To do the migration yourself, check out the `start` branch.

## Tested with

Java 25, Spring Boot 4.2.0.

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

- [CarrierClientConfig.java](src/main/java/dev/danvega/tracker/carrier/CarrierClientConfig.java) builds the `RestClient` from the auto-configured `RestClient.Builder`.
- [CarrierClient.java](src/main/java/dev/danvega/tracker/carrier/CarrierClient.java) makes the calls and handles the 404 with `onStatus`.
- [ShipmentControllerTest.java](src/test/java/dev/danvega/tracker/ShipmentControllerTest.java) tests the API with `RestTestClient`.

## What changed

| RestTemplate (`start`) | RestClient (`main`) |
| --- | --- |
| `RestTemplateBuilder` bean | `RestClient.Builder` bean |
| `rootUri(...)` | `baseUrl(...)` |
| `connectTimeout` and `readTimeout` in code | `spring.http.clients.connect-timeout` and `read-timeout` in `application.yaml` |
| `RestTemplateCustomizer` with an interceptor | `RestClientCustomizer` with `defaultHeader` |
| `getForObject(...)` | `get().uri(...).retrieve().body(...)` |
| `exchange(...)` with `ParameterizedTypeReference` | `retrieve().body(new ParameterizedTypeReference<>() {})` |
| `CarrierErrorHandler` class | one `onStatus(...)` call |
| `@RestClientTest` with relative URLs | `@RestClientTest` with full URLs |
| `TestRestTemplate` and `@AutoConfigureTestRestTemplate` | `RestTestClient` and `@AutoConfigureRestTestClient` |

## Branches

| Branch | What it has |
| --- | --- |
| `start` | Spring Boot 4.1.3 with `RestTemplate`. Start here. Its README lists the migration steps. |
| `main` | Spring Boot 4.2.0 with `RestClient`. The finished code. |

## Good to know

- `carrier-api` is a stand-in for a real carrier. It never changes during the migration. It expects the header `X-Api-Key: demo-key`.
- The `spring.http.clients.*` timeouts apply to every HTTP client Boot builds for you, not just this one.
- `RestClientCustomizer` applies to every `RestClient.Builder` from the context, the same way `RestTemplateCustomizer` did. In a bigger app, set the API key on the carrier client only.
- With `rootUri`, Boot's `MockRestServiceServer` matched relative paths like `/shipments`. With `baseUrl`, the test expectations need the full URL.
