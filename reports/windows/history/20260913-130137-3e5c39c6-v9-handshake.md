# Client V9 handshake capture Windows

- Tested commit: 3e5c39c651990cac16d621a564016e4d59f559d3
- Status: CAPTURED
- Server READY before client attempt: True
- Server process stopped by runner: true
- Database migration/import performed: false
- Authentication audit fields may change if login reached AuthenticationService: true

## Standard output

    NSOCry server started on /0.0.0.0:14444
    DATA runtime snapshot READY version=7

## Sanitized standard error

    HANDSHAKE_COMPLETED remote=/127.0.0.1:59998 outcome=AUTHENTICATED
    HANDSHAKE_COMPLETED remote=/127.0.0.1:60001 outcome=AUTHENTICATED
    HANDSHAKE_COMPLETED remote=/127.0.0.1:60004 outcome=AUTHENTICATED
