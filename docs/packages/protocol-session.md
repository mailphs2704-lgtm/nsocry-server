# Protocol and session bootstrap

## Scope

This package checkpoint implements only the verified connection/bootstrap behavior needed before database or gameplay work. It does not authenticate against a database, select a character, load maps or start a production listener.

## Package boundaries

| Package | Responsibility |
|---|---|
| `com.nsocry.protocol.compat` | Exact legacy-client wire compatibility: key delta, rolling XOR, frame encode/decode, bounded stream I/O |
| `com.nsocry.session` | Explicit handshake phases, payload decoding, authentication port and deterministic transport close |
| `com.nsocry.network` | Bounded TCP acceptor, socket options, named session ownership and event port |

The compatibility package must not contain gameplay or persistence logic. New internal services must not depend on obfuscated/reference class names.

## Session phases

```text
CONNECTED
  -> KEY_SENT
  -> CLIENT_INFO_RECEIVED
  -> LOGIN_PENDING
  -> AUTHENTICATED
  -> CLOSED
```

A rejected login returns from `LOGIN_PENDING` to `CLIENT_INFO_RECEIVED`, allowing a bounded retry policy to be added outside the state machine. `CLOSED` is terminal and close is idempotent.

## Implemented classes

### Wire compatibility

- `RollingXorCipher`: one cursor per direction; never reset per frame.
- `LegacyKeyCodec`: delta key encode/decode.
- `LegacyFrameCodec`: in-memory short and full-size vectors.
- `LegacyFrameReader`: streaming read with configured limits before payload allocation.
- `LegacyFrameWriter`: synchronized write + flush.
- `ProtocolLimits`: short/full payload limits.
- `ProtocolFrame`: defensive-copy frame value.

### Session bootstrap

- `HandshakeStateMachine`: atomic validated transitions.
- `LegacySessionTransport`: validates the empty `-27` trigger, sends key, activates independent ciphers and closes deterministically.
- `HandshakePayloadDecoder`: client-accurate CLIENT_INFO and LOGIN decoding.
- `LoginRequest`: redacts password and client token from `toString()`.
- `AuthenticationPort`: boundary for the future account/authentication module.
- `SessionKeyProvider`: boundary for per-session key creation.
- `SecureRandomSessionKeyProvider`: production-safe random key source; key length is explicitly bounded.
- `HandshakeProcessor`: orders CLIENT_INFO and LOGIN without database coupling.

### TCP lifecycle

- `TcpServerConfig`: bind address, backlog, maximum concurrent sessions, read timeout and shutdown timeout.
- `TcpServer`: loopback/real bind, named accept/session threads, zero-capacity handoff, overload rejection and graceful close.
- `SessionConnectionHandler`: application boundary for each accepted socket.
- `LegacyHandshakeConnectionHandler`: composes socket streams, transport, processor, key provider and authentication port for one bootstrap attempt.
- `NetworkEventSink`: explicit sanitized failure/rejection reporting; network exceptions are not silently swallowed.

## Security properties at this checkpoint

- Frame sizes are bounded before streaming payload allocation.
- Client-to-server full-size frames are rejected because the inspected client does not emit them.
- Password/token are redacted from the login object's string representation.
- No logging framework or credential logging exists in this layer.
- Transport close is idempotent.
- The weak legacy cipher is isolated and is not treated as authentication/integrity.

## Tests

The test suite currently covers:

1. fixture key encoding/reconstruction;
2. continuous cursor across four client frames;
3. 32 KiB full-size vector and hash;
4. legal/illegal/terminal state transitions;
5. bounded streaming and directional full-size rejection;
6. trigger → key → encrypted client frame;
7. exact CLIENT_INFO byte/int order;
8. LOGIN reserved fields and secret redaction;
9. trigger → CLIENT_INFO → accepted LOGIN processor flow;
10. loopback TCP accept and graceful shutdown;
11. full loopback trigger → key → CLIENT_INFO → LOGIN with fake authentication.

Expected total after this checkpoint: 16 tests. The original 15-test checkpoint is VERIFIED on Windows; the new loopback test awaits Windows Maven verification.

## Not implemented yet

- application bootstrap/main entry point;
- production observability implementation for `NetworkEventSink`;
- login attempt rate limiting;
- account database adapter and password hashing;
- version/update response;
- character list/selection;
- map entry.

## Next exact action

Run the new 16-test suite on Windows. If VERIFIED, add a composition/bootstrap entry point with sanitized network event reporting and configuration loading. Do not connect the database yet.
