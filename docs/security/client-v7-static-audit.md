# V7_217_X1 client static safety audit

> Scope: static bytecode/resource inspection only. Neither the client nor NSOKISS was executed.

## Decision

Do **not** modify or repack the supplied JAR at this checkpoint. No evidence of analytics, telemetry, hidden HTTP tracking, IMEI/IMSI harvesting, contact access or location collection was found. Repacking an obfuscated Java ME client without a demonstrated defect would add compatibility risk and make the artifact harder to verify.

The original artifact SHA-256 is:

`affd33efffe2962c90c7e1da696d273ef9ac07ce27b81623afe8f364d2f32dd1`

## Findings

| Capability | Evidence | Assessment |
|---|---|---|
| Game socket | `SocketConnection` in obfuscated session/connect classes | Required for gameplay |
| Rolling-XOR key | Independent read/write cursors and delta reconstruction | Weak security, but required for current wire compatibility |
| SMS sending | `MessageConnection` in classes `N`/`O` | Server-triggered SMS payment flow; not evidence of tracking |
| External links | `platformRequest` call sites | User/UI or server-provided link handling; no embedded tracker URL found |
| Local storage | Java ME RMS `RecordStore` | Expected client preferences/cache/account storage; inspect further before claiming contents |
| Device identifiers | No IMEI, IMSI, MSISDN or subscriber API/string evidence | Not found in static scan |
| Analytics/telemetry | No analytics/telemetry endpoint or library evidence | Not found in static scan |
| Signatures | No JAR signature files under `META-INF` | Repacking would not invalidate a present signature, but still changes artifact identity |

## SMS path detail

The receiver handles a server message branch, reads two UTF strings, creates success/failure UI callbacks and calls an SMS helper. That helper opens the supplied SMS address, creates a text message, sets its address/payload and sends it. This appears to be a legacy payment/top-up feature initiated by protocol/UI flow.

It must not be renamed “tracking” without additional evidence. If NSOCry does not support SMS payment, the safe approach is to stop emitting that server command. Binary removal from the client is unnecessary and could break verifier/control flow.

## Key/cipher decision

The rolling XOR does not provide modern confidentiality, integrity or server authentication. It must never be relied upon to protect passwords. However, changing the algorithm only in the JAR would make it incompatible with the reference wire protocol.

NSOCry therefore:

1. isolates it in `com.nsocry.protocol.compat`;
2. never logs credentials;
3. plans a separate negotiated protocol v2/TLS path later;
4. preserves the current byte behavior only for legacy-client compatibility.

## Conditions that justify a clean rebuilt JAR

Create a modified JAR only after at least one of these is true:

- a concrete unwanted endpoint or exfiltration path is proven;
- an unsafe capability cannot be disabled server-side;
- a client-side protocol bug blocks NSOCry compatibility;
- the full transformed class set can be rebuilt and verified on the target Java ME devices.

Any rebuilt artifact must receive a new NSOCry name/version, checksum, change manifest and device compatibility test matrix. It must not silently replace the original reference JAR.

## Remaining audit work

- Decode and classify every `platformRequest` source.
- Inventory RMS store names and stored record schemas.
- Map the server command that activates the SMS branch to the command inventory.
- Confirm whether the target devices prompt the user before SMS send.
