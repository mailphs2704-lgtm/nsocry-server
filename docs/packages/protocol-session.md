# Protocol and session bootstrap

## Scope

This package checkpoint implements only the verified connection/bootstrap behavior needed before database or gameplay work. It does not authenticate against a database, select a character, load maps or start a production listener.

## Package boundaries

| Package | Responsibility |
|---|---|
| `com.nsocry.protocol.compat` | Exact legacy-client wire compatibility: key delta, rolling XOR, frame encode/decode, bounded stream I/O |
| `com.nsocry.session` | Explicit handshake phases, payload decoding, authentication port and deterministic transport close |

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
- `HandshakeProcessor`: orders CLIENT_INFO and LOGIN without database coupling.

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
9. trigger → CLIENT_INFO → accepted LOGIN processor flow.

Expected total after the next pull: 14 tests.

## Not implemented yet

- production `ServerSocket` accept loop;
- session/thread ownership and backpressure policy;
- secure key generation policy;
- login attempt rate limiting;
- account database adapter and password hashing;
- version/update response;
- character list/selection;
- map entry.

## Next exact action

Add a minimal TCP acceptor around `LegacySessionTransport` with bounded active sessions, read timeout, named thread ownership and graceful shutdown. Use a fake authentication adapter in tests; do not connect the database yet.
