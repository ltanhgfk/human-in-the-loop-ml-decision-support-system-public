# Build / Reproduction Notes

This is a historical Java web/application codebase. The public repository intentionally excludes compiled binaries, private runtime configuration, trained models, historical datasets, and vendored dependency JARs.

## Public reconstruction

1. Install a compatible Java development environment for the original source.
2. Obtain the required third-party dependencies independently and verify their licenses.
3. Copy `config/db.properties.example` to a local configuration file and provide local database settings.
4. Copy `config/SMSServer.conf.example` to the runtime configuration location and provide local SMS gateway settings if SMS integration is being reproduced.
5. Do not use historical/private data when reproducing experiments.

Because the original deployment depended on legacy SMS/MMS infrastructure, exact end-to-end reproduction may require the historical gateway environment and is not guaranteed on a modern machine.
