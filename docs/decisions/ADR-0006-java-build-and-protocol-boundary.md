# ADR-0006 — Java build baseline and legacy protocol boundary

- Status: Accepted
- Date: 2026-08-18

## Context

NSOCry is a complete rewrite that must remain compatible with the supplied Java ME client. The existing rolling-XOR handshake is weak as a security mechanism, but changing it unilaterally would break that client. The new implementation also needs a build baseline that is easy to install on Windows and stable enough for long-term server work.

## Decision

1. Use Java 17 as the minimum runtime and compilation target.
2. Use Maven with a single module during the protocol/bootstrap stage.
3. Use package root `com.nsocry`.
4. Put wire-compatibility code under `com.nsocry.protocol.compat`.
5. Keep the rolling-XOR/key-delta algorithm only inside that compatibility boundary.
6. Do not describe the legacy cipher as authentication, integrity protection or credential security.
7. Use JUnit 6 for automated tests and keep protocol vectors in repository fixtures.
8. Split modules only after real dependency boundaries appear; do not create empty architecture layers.

## Consequences

- The first code checkpoint can be compiled with any supported JDK 17+ installation.
- Compatibility defects can be reproduced and tested without leaking into domain/gameplay code.
- A future protocol v2 may add authenticated encryption or TLS without rewriting gameplay services.
- Legacy clients remain supported through an explicit adapter rather than defining NSOCry's internal architecture.

## Rejected alternatives

- Editing the client key algorithm immediately: rejected because it breaks compatibility unless both endpoints and deployment are migrated together.
- Copying the reference session/network classes: rejected by project requirements and because their concurrency/security defects are already documented.
- Starting with many Maven modules: rejected until dependency boundaries are proven by implementation.

## Verification

- Main protocol classes compile with `javac --release 17`.
- Manual fixture verification passed for key frame and full-size frame SHA-256.
- New source contains no legacy project/vendor identifiers.
