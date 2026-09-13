# NSOKISS handshake and login protocol

> Status: server-side reference and the listed client-side behaviors are VERIFIED by static inspection. No NSOKISS runtime test was performed.

## 1. Session state model observed

```text
TCP_ACCEPTED
  -> FIRST_FRAME_RECEIVED
  -> KEY_SENT
  -> CLIENT_INFO_RECEIVED
  -> LOGIN_REQUESTED
  -> USER_LOADED
  -> VERSION_SENT
  -> CLIENT_OK
  -> CHARACTER_LIST_SENT
  -> CHARACTER_SELECTED
  -> IN_MAP
```

Important: the implementation uses booleans rather than an explicit state machine. The model above is documentation, not a claim that an enum/state class exists.

Observed flags:

| Flag | Meaning observed |
|---|---|
| `connected` | socket/session loop may continue |
| `sendKeyComplete` | key was sent; subsequent frame bytes use XOR transform |
| `isSetClientType` | CLIENT_INFO payload parsed once |
| `isLogin` | login attempt currently executing |
| `isLoginSuccess` | User load finished successfully |
| `clientOK` | client acknowledged version/data stage |
| `isClosed` | close sequence already started |

## 2. Initial frame and key exchange

### 2.1 First client frame

`MessageCollector` calls `readMessage()` immediately after TCP accept.

Before `sendKeyComplete`:

```text
byte   command
ushort payloadLength   (DataInputStream.readUnsignedShort, network/big-endian)
byte[] payload
```

After the first message is read, the collector checks `sendKeyComplete == false` and calls `sendKey()` instead of `processMessage(message)`.

**VERIFIED consequence:** the first decoded client message is used only as a key-exchange trigger and is not dispatched to Controller. Client bytecode confirms it intentionally sends command `GET_SESSION_ID (-27)` with a zero-length payload immediately after opening the streams.

### 2.2 Server key frame

`sendKey()` generates a key, creates command `GET_SESSION_ID (-27)`, and sends the frame before setting `sendKeyComplete = true`. Therefore this response is unencrypted.

Frame:

```text
command = GET_SESSION_ID (-27)
ushort payloadLength
payload:
  byte keyLength
  byte key0
  repeat i = 1 .. keyLength-1:
    byte (key[i] XOR key[i-1])
```

Client reconstruction implied by the encoding:

```text
key[0] = transmittedKey0
key[i] = transmittedDelta[i] XOR key[i-1]
```

Reference key generation:

```text
bytes("nsoz_" + serverId * random(0..9999))
```

The literal legacy prefix is **reference behavior**, not an allowed NSOCry identifier. Because the whole key is transmitted, changing generation may be compatible, but this must be verified against the client before deciding.

### 2.3 Cipher transform

Read and write use independent cursors `curR` and `curW`.

```text
plainOrCipherByte XOR key[cursor]
cursor = (cursor + 1) mod keyLength
```

After key exchange, the transform is applied continuously in wire order to:

1. command byte;
2. two length bytes;
3. every payload byte.

Read and write cursor streams are independent.

## 3. Normal inbound frame

After `sendKeyComplete`:

```text
xor(command)
xor(lengthHigh)
xor(lengthLow)
xor(payload[0..n-1])
```

Server decrypts in the same order and rejects `size > Config.messageSizeMax`.

### Limitation observed

Server inbound `readMessage()` uses a two-byte length. Client static analysis confirms its outbound writer also uses two bytes. For server-to-client traffic, the client recognizes decoded command `-32` and reads a four-byte encrypted big-endian length. The earlier asymmetry is therefore intentional at wire level, although special commands `-31`/`-32` still need semantic names and boundary tests.

## 4. Envelope dispatch

Login-stage client messages use outer command `NOT_LOGIN (-29)`.

Outer payload begins with:

```text
byte nestedCommand
...nestedPayload
```

Controller allows `NOT_LOGIN` without an attached User/Char and dispatches to `messageNotLogin`.

Verified nested routes:

| Nested command | Value | Handler |
|---|---:|---|
| `CLIENT_INFO` | -125 | `Session.setClientType` |
| `LOGIN` | -127 | `Session.login` |

The numeric values collide with symbols in other scopes; envelope and session phase are required to identify them.

## 5. CLIENT_INFO payload

Read once by `Session.setClientType`:

| Order | Java read | Reference field | Status |
|---:|---|---|---|
| 1 | `readByte` | clientType | VERIFIED |
| 2 | `readByte` | zoomLevel | VERIFIED; clamped to 1..4 |
| 3 | `readBoolean` | isGPS | VERIFIED |
| 4 | `readInt` | width | VERIFIED |
| 5 | `readInt` | height | VERIFIED |
| 6 | `readBoolean` | isQwert | VERIFIED |
| 7 | `readBoolean` | isTouch | VERIFIED |
| 8 | `readUTF` | platform (reference typo: `plastfrom`) | VERIFIED |
| 9 | `readInt` | legacy combined field | SERVER READ VERIFIED; client writes byte `0` here |
| 10 | `readByte` | legacy combined field | SERVER READ VERIFIED; client writes int `0` across positions 9–10 |
| 11 | `readByte` | languageId | VERIFIED |
| 12 | `readInt` | provider | VERIFIED name only |
| 13 | `readUTF` | agent | VERIFIED name only |

After parsing, language is resolved from `GameData` and `isSetClientType` becomes true.

**Client compatibility note:** the inspected client writes a byte then an int for fields 9–10, while the server reads an int then a byte. Total width remains five bytes and both client values are zero, so later fields stay aligned. NSOCry must model the actual wire bytes in its compatibility adapter instead of copying these legacy field types.

## 6. LOGIN payload

Read by `Session.login`:

| Order | Java read | Reference field/use | Status |
|---:|---|---|---|
| 1 | `readUTF().trim()` | username | VERIFIED |
| 2 | `readUTF().trim()` | password | VERIFIED |
| 3 | `readUTF().trim()` | version | VERIFIED |
| 4 | `readUTF()` | empty string in this client build | CLIENT VERIFIED |
| 5 | `readUTF()` | empty string in this client build | CLIENT VERIFIED |
| 6 | `readUTF().trim()` | obfuscated helper result | Wire presence VERIFIED; semantics UNKNOWN |
| 7 | `readByte()` | client global/server selection | Wire presence VERIFIED; mapping UNKNOWN |

`DataInputStream.readUTF` uses Java modified UTF-8 with a two-byte unsigned encoded length.

## 7. Login validation/order

Observed order:

1. Parse payload.
2. Write username, **plaintext password**, and IP to console/file.
3. Store version and apply per-username login-attempt throttle.
4. Parse numeric version by removing dots.
5. Require:
   - connected;
   - server not in maintenance;
   - CLIENT_INFO already received;
   - key exchange completed.
6. Reject repeat/concurrent session login flags.
7. Construct `User(session, username, password, random)`.
8. Call `User.login()`.
9. On `isLoadFinish`:
   - bind User to Session;
   - bind User/Service to Controller;
   - call `Service.updateVersion()`.
10. Otherwise record login failure.

## 8. User authentication/load behavior

Verified from `User.login/getUserMap`:

- Username must match `^[a-zA-Z0-9]+$`.
- User row is loaded from `users`.
- Reference compares stored password directly with the supplied plaintext string.
- Maintenance allowlist/time is loaded from server data.
- Ban/status fields are checked.
- Duplicate in-memory and database-online states are checked.
- IP history is updated in memory.
- User is registered online and `isLoadFinish = true`.

Database/table detail still needs a separate table-to-code matrix.

## 9. Server response after successful User load

`Service.updateVersion()` sends:

```text
outer command: NOT_MAP (-28)
nested command: UPDATE_VERSION (-123)
payload: raw bytes of Server.version
```

The exact length/structure of `Server.version` must be documented from its declaration/initialization before calling the payload fully specified.

## 10. CLIENT_OK and character list

Client later sends:

```text
outer: NOT_MAP
nested: CLIENT_OK (-101)
```

`Session.clientOk()`:

1. sets `clientOK`;
2. calls `User.initCharacterList()`;
3. sends character list with outer `NOT_MAP`, nested `SELECT_PLAYER (-126)`.

Character-list payload:

```text
byte count
repeat count:
  byte gender
  UTF name
  UTF school
  byte level
  short head
  short weapon
  short body
  short leg
```

Client bytecode confirms the character selection request is outer `NOT_MAP`, nested `SELECT_PLAYER`, then exactly one modified-UTF character name. Server `User.selectChar` loads/binds the character and eventually calls `MapManager.joinZone`; exact response ordering inside `loadAll()` and `joinZone()` remains pending.

## 11. Critical legacy defects — must not copy to NSOCry

| Severity | Reference behavior | NSOCry requirement |
|---|---|---|
| CRITICAL | Logs plaintext username/password to console and file | Never log credentials; redact sensitive fields |
| CRITICAL | Direct plaintext password equality | Use modern password hashing and migration strategy |
| HIGH | Predictable key generation with non-cryptographic random | Decide threat model; use safe randomness if protocol permits |
| HIGH | Exceptions swallowed in network send/read paths | Structured errors, metrics and deterministic close |
| HIGH | First frame silently discarded as handshake trigger | Explicit handshake state/command in NSOCry implementation |
| HIGH | Boolean flags approximate a state machine | Use explicit validated session transitions |
| MEDIUM | Login throttle maps appear unbounded/unsynchronized | Bounded concurrent rate limiter with cleanup |
| MEDIUM | Sender uses mutable ArrayList across threads | Thread-safe bounded queue/backpressure |
| MEDIUM | Inbound/outbound large-frame behavior is asymmetric | Specify and test both directions |

These are findings about reference quality, not permission to change wire behavior without client compatibility testing.

## 12. NSOCry design constraints derived

- Namespace/identifier follows ADR-0005: `NSOCry/nsocry/Cry/cry`, package root `com.nsocry`.
- Transport, frame codec, handshake state, authentication and character-list use cases must be separate.
- Codec tests must cover cursor continuity across command/length/payload and across multiple messages.
- Sensitive payloads must never appear in logs.
- Legacy key prefix/string must not be copied blindly.
- Compatibility decisions require client JAR or captured fixture evidence.

## 13. Remaining UNKNOWN / next work

1. Resolve `Server.version` payload and client update negotiation.
2. Identify LOGIN field 6 semantics and the selected-server byte mapping.
3. Trace error/dialog responses for each login rejection.
4. Trace exact packet ordering in `loadAll()` and `joinZone()`.
5. Name and boundary-test special large-frame commands `-31`/`-32`.
6. Implement the documented deterministic compatibility fixture.

Full client evidence and fixture specification: [client-jar-analysis.md](client-jar-analysis.md).

## Evidence

- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/network/Session.java`
- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/network/Controller.java`
- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/network/Message.java`
- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/network/AbsService.java`
- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/network/Service.java`
- `source-reference/NSOKISS-inspection/src/main/java/com/nsoz/model/User.java`


## 14. Client-side verification checkpoint

Static bytecode inspection verified the initial `-27` empty trigger, delta key reconstruction, independent rolling cursors, CLIENT_INFO/LOGIN construction, CLIENT_OK and SELECT_PLAYER request. See [client-jar-analysis.md](client-jar-analysis.md). The client and NSOKISS server were not executed.
