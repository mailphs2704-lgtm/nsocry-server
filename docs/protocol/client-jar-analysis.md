# Client JAR static analysis — V7_217_X1

> Status: VERIFIED by static bytecode inspection on 2026-08-18. No NSOKISS server or client runtime test was performed.

## Scope and rule

The supplied `V7_217_X1.jar` is an obfuscated Java ME client. It is used only to verify wire behavior that server-side source could not prove. Obfuscated class and method names below are evidence locators, not names to reuse in NSOCry.

NSOCry naming remains `NSOCry/nsocry/Cry/cry`; legacy vendor/product strings found in the reference JAR are not permitted as new implementation identifiers.

## Artifact identity

Manifest evidence:

| Field | Value |
|---|---|
| MIDlet | `GameMidlet` |
| MIDlet name | `V7_217_X1` |
| Version | `1.0.0` |
| Profile | MIDP 2.0 |
| Configuration | CLDC 1.1 |
| Class count | 180 |

The JAR is heavily obfuscated, so findings are based on Java bytecode instructions and data-stream calls rather than guessed source names.

## Verified transport handshake

### Connection trigger

Obfuscated class `K` opens a `SocketConnection`, opens output/input streams, starts sender and receiver threads, then immediately queues a message whose command byte is `-27` and whose payload is empty.

Therefore the first client frame is:

```text
command: -27
length: 0 (unsigned short, big-endian)
payload: empty
```

This confirms that the server-side discarded first frame is not arbitrary: the compatible client intentionally sends an empty `GET_SESSION_ID (-27)` trigger.

### Key response parsing

Receiver class `L` handles incoming command `-27` specially:

1. reads one signed byte as key length;
2. allocates the key array;
3. reads that many encoded bytes;
4. reconstructs in place for `i = 0 .. length-2`:
   `key[i + 1] = key[i + 1] XOR key[i]`;
5. enables encrypted framing.

This exactly matches the server encoding `key[0]`, followed by `key[i] XOR key[i-1]`.

### Rolling cipher

Session class `bQ` contains two independent byte cursors. Both transforms XOR the byte with `key[cursor]`, increment the cursor, and wrap modulo key length. One cursor is used by outbound framing and the other by inbound framing.

Verified wire order after handshake:

1. command;
2. length bytes;
3. payload bytes.

Cursor state continues across fields and messages; it is not reset per frame.

## Verified frame sizes

Client outbound writer:

- writes a two-byte big-endian length;
- encrypts both length bytes when the key is active;
- encrypts every payload byte;
- exception: reference command `-31` writes its length without the normal encrypted-length branch.

Client inbound reader:

- normally reads an encrypted two-byte length;
- if decoded command is `-32`, reads four encrypted bytes and combines them as a big-endian 32-bit length;
- before key activation, reads an ordinary unsigned-short length.

This resolves the earlier asymmetry: large server-to-client frames are supported by the client through command `-32`. Client-to-server framing remains two-byte in the inspected sender.

## Verified CLIENT_INFO request

Obfuscated client service class `bP`, method `fi()`, constructs nested command `CLIENT_INFO (-125)` using the `NOT_LOGIN (-29)` envelope.

Payload order and concrete client values:

| Order | Type | Client source/value | Meaning |
|---:|---|---|---|
| 1 | byte | `GameMidlet.w` | client type |
| 2 | byte | `cn.c` | zoom level |
| 3 | boolean | `ar.s` | GPS capability/state |
| 4 | int | `ar.cU` | screen width |
| 5 | int | `ar.cV` | screen height |
| 6 | boolean | `cd.bb` | QWERTY capability/state |
| 7 | boolean | `ar.ba` | touch capability/state |
| 8 | UTF | `System.getProperty(decodedKey)` | platform property |
| 9 | byte | constant `0` | previously mismatched with server `readInt` |
| 10 | int | constant `0` | previously mismatched with server `readByte` |
| 11 | byte | `cg.b` | language |
| 12 | int | constant `0` | provider |
| 13 | UTF | constant `"0"` | agent |

### Compatibility defect found

The inspected client writes field 9 as **byte** then field 10 as **int**, while the reference server reads field 9 as **int** then field 10 as **byte**. Both consume five bytes, so subsequent fields remain aligned, but the two values are semantically merged/reinterpreted by the server.

For this client both values are zero, so behavior appears to work. NSOCry must preserve byte-level compatibility in its legacy adapter and must not treat the server-side legacy field names/types as authoritative semantics.

## Verified LOGIN request

Obfuscated method `b(String, String, String)` calls `fi()` first, then sends nested `LOGIN (-127)` inside `NOT_LOGIN (-29)`.

Payload:

| Order | Type | Client value |
|---:|---|---|
| 1 | UTF | first argument: username |
| 2 | UTF | second argument: password |
| 3 | UTF | third argument: client version |
| 4 | UTF | empty string |
| 5 | UTF | empty string |
| 6 | UTF | value returned by an obfuscated helper from a decoded static key |
| 7 | byte | `GameMidlet.x` |

The two unnamed UTF fields are now VERIFIED as empty for this client build. The sixth field is generated/loaded through an obfuscated helper; its business meaning remains UNKNOWN. The final byte is a client global used as the selected server identifier/index; its exact domain mapping remains pending.

The client sends CLIENT_INFO immediately before each LOGIN call, confirming the server-side order requirement.

## Verified post-login requests

### CLIENT_OK

Obfuscated method `fx()` sends nested `CLIENT_OK (-101)` in the `NOT_MAP (-28)` envelope with no additional payload.

### Character selection

Obfuscated method `q(String)` sends:

```text
outer: NOT_MAP (-28)
nested: SELECT_PLAYER (-126)
payload: UTF characterName
```

This matches `User.selectChar`, which reads exactly one UTF character name.

The reference server then performs character load, binds the character to controller/service, loads inventory/state, sends box and general data, and calls `MapManager.joinZone`. The exact server-to-client packet ordering inside `loadAll()` and `joinZone()` remains a separate discovery task.

## Compatibility fixture specification

The first deterministic fixture for NSOCry should cover:

1. raw trigger bytes: `E5 00 00` (`-27`, zero length);
2. an unencrypted key response with a fixed test key;
3. client reconstruction of delta-encoded key bytes;
4. independent read/write rolling cursors;
5. encrypted CLIENT_INFO using the exact byte/int order written by this client;
6. encrypted LOGIN with empty UTF fields 4 and 5;
7. CLIENT_OK with no nested payload;
8. SELECT_PLAYER with one Java modified-UTF character name;
9. inbound four-byte full-size parsing for command `-32`.

Fixtures must use a fixed synthetic NSOCry test key and synthetic credentials. Never include real credentials or the legacy generated prefix.

## Remaining unknowns

- decoded platform-property key used by `System.getProperty`;
- semantics/source of LOGIN field 6;
- exact mapping of `GameMidlet.x` server identifier;
- precise version/data response payload and update negotiation;
- server-to-client packet order during `loadAll()` and `joinZone()`;
- meaning and compatibility requirement of special commands `-31` and `-32`.

## Evidence

- supplied artifact: `V7_217_X1.jar`;
- bytecode classes: `K` (connect/trigger), `L` (receiver/key/full-size), `bQ` (session/cipher/sender), `bP` (request construction);
- reference server source used only for cross-checking: `Session`, `Controller`, `User`, `Service`.

No runtime execution of NSOKISS was used or required.
