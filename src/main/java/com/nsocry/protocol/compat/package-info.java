/**
 * Isolates the wire-format compatibility required by the legacy V7 client:
 * framing, bounded stream I/O, key encoding and rolling XOR state.
 *
 * <p>The cipher is compatibility-only and must not be treated as security.</p>
 */
package com.nsocry.protocol.compat;
