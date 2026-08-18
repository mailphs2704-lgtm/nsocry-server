# Code reference: protocol, session and network

## Documentation contract

This document is the method-level navigation index for the first NSOCry runtime checkpoint. Source remains authoritative. Every source change must update its Javadoc and the matching entry here. New packages require a package-info.java.

## Package map

| Package | Purpose | Must not contain |
|---|---|---|
| com.nsocry.protocol.compat | V7 wire compatibility and bounded stream I/O | Authentication, persistence, gameplay |
| com.nsocry.session | Handshake order, decoded values and application ports | Socket accept loop, database implementation |
| com.nsocry.network | Listener lifecycle, limits and socket/session composition | Protocol field decoding, gameplay |

## Protocol compatibility reference

| Class/member | Purpose | Contract and failure behavior |
|---|---|---|
| ProtocolFrame | Immutable command/payload value | Payload is defensively copied |
| ProtocolLimits / requireAllowed | Defines and enforces allocation ceilings | Rejects invalid ranges, negative and oversized lengths |
| RollingXorCipher constructor | Creates one directional cursor | Empty key rejected; never share across directions |
| transform(byte/byte[]) | Applies compatibility transform and advances cursor | Array input is copied; cursor remains continuous across frames |
| cursor | Exposes current position for verification | Diagnostic only |
| LegacyKeyCodec.encodePayload | Delta-encodes the session key for command -27 | Key must contain 1–255 bytes |
| LegacyKeyCodec.decodePayload | Reconstructs a transmitted key | Rejects malformed declared length |
| LegacyFrameCodec.encodeShortFrame | Builds command + unsigned-short length + payload | Optional cipher; maximum 65535 bytes |
| encodeFullSizeFrame | Builds -32 + int length + payload | Server-to-client compatibility path |
| decodeFrame | Decodes complete in-memory fixture | Rejects short/mismatched data |
| LegacyFrameReader constructor | Wraps inbound stream and limits | Maintains continuous inbound cursor |
| readUnencryptedShortFrame | Reads initial key trigger | Used before cipher activation |
| readEncryptedFrame | Reads encrypted short/full frame | Can forbid client full-size frames; checks length before allocation |
| LegacyFrameWriter constructor | Wraps outbound stream and limits | Writes are synchronized and flushed |
| writeUnencryptedShortFrame | Sends plaintext key response | Pre-cipher path only |
| writeEncryptedShortFrame | Sends normal encrypted frame | Requires outbound cipher |
| writeEncryptedFullSizeFrame | Sends large encrypted server payload | Enforces full payload limit |

## Session reference

| Class/member | Purpose | Contract and state effect |
|---|---|---|
| SessionPhase | Connection/login lifecycle values | CLOSED is terminal |
| HandshakeEvent | Result of one processor step | Separate from mutable phase |
| AuthenticationDecision | Authentication boundary result | ACCEPTED or REJECTED |
| ClientInfo | Decoded client capabilities | Field order follows verified wire order |
| LoginRequest | Decoded login data | String representation redacts password and token |
| LoginRequest accessors | Supply authentication inputs | Secrets must never be logged |
| ProtocolStateException | Illegal transition report | Contains phases only, never credentials |
| HandshakeStateMachine.phase | Reads phase atomically | No mutation |
| keySent | CONNECTED to KEY_SENT | Otherwise throws state exception |
| clientInfoReceived | KEY_SENT to CLIENT_INFO_RECEIVED | Enforces order |
| loginStarted | CLIENT_INFO_RECEIVED to LOGIN_PENDING | Enforces order |
| loginSucceeded | LOGIN_PENDING to AUTHENTICATED | Records success |
| loginRejected | LOGIN_PENDING to CLIENT_INFO_RECEIVED | Leaves room for future bounded retry |
| close | Any live phase to CLOSED | Idempotent |
| isAuthenticated / isClosed | State queries | No mutation |
| HandshakePayloadDecoder.decodeClientInfo | Decodes nested -125 payload strictly | Rejects envelope, command, truncation and trailing bytes |
| decodeLogin | Decodes nested -127 payload strictly | Preserves secret values only for authentication |
| AuthenticationPort.authenticate | Account-module boundary | Current test uses fake adapter only |
| SessionKeyProvider.createKey | Supplies one key per connection | Policy stays outside transport |
| SecureRandomSessionKeyProvider constructor | Configures random key length | Accepts 1–255 |
| createKey | Creates fresh SecureRandom bytes | Called once per handshake |
| LegacySessionTransport constructor | Owns frame I/O, ciphers and close target | One instance per client |
| beginHandshake | Validates -27 trigger, sends key, activates two ciphers | Advances to KEY_SENT |
| readClientFrame | Reads next encrypted inbound frame | Requires key exchange |
| sendShortFrame / sendFullSizePayload | Sends encrypted server data | Requires key exchange |
| state | Exposes phase machine to processor | Ownership retained |
| close | Closes state and underlying target once | Idempotent |
| HandshakeProcessor constructor | Connects transport to ordered decoding | No database dependency |
| begin | Starts key exchange | Returns KEY_ESTABLISHED |
| receiveNext | Dispatches CLIENT_INFO or LOGIN by phase | Rejects unsupported order |
| clientInfo | Returns accepted metadata | Null before CLIENT_INFO |

## Network reference

| Class/member | Purpose | Contract and state effect |
|---|---|---|
| TcpServerConfig | Validates bind, backlog, session and timeout values | Rejects unsafe/non-positive configuration |
| SessionConnectionHandler.handle | Boundary for one accepted socket | TcpServer owns final socket close |
| NetworkEventSink callbacks | Report accept/session/rejection failures | Implementations must sanitize output |
| TcpServer constructor | Creates bounded session executor | Zero-capacity handoff prevents hidden queue growth |
| start | Binds and starts accept thread | Double start rejected; bind failure rolls back |
| isRunning | Reads listener state | Atomic |
| localAddress | Returns actual bound address | Requires bound listener |
| close | Stops listener, sessions and accept thread | Idempotent; timeout and interrupt aware |
| LegacyHandshakeConnectionHandler constructor | Composes limits, key and authentication ports | All dependencies mandatory |
| handle | Runs key, CLIENT_INFO and LOGIN for a socket | Only AUTHENTICATED or LOGIN_REJECTED is terminal |

## Test ownership

| Test | Protected behavior |
|---|---|
| ProtocolFixtureTest | Key, cipher and frame fixtures |
| LegacyFrameStreamTest | Stream limits and directional full-size rule |
| HandshakeStateMachineTest | Legal, rejected and terminal transitions |
| LegacySessionTransportTest | Trigger, key, encrypted input and close |
| HandshakePayloadDecoderTest | Exact field order and secret redaction |
| HandshakeProcessorTest | Ordered flow with fake authentication |
| TcpServerTest | Loopback accept and graceful shutdown |
| LegacyHandshakeLoopbackTest | Real socket trigger, key, CLIENT_INFO and LOGIN |
