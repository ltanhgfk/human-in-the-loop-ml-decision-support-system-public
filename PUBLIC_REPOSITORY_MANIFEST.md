# Public Repository Manifest

## Kept

- Java source code
- web/application source
- database schema without historical records
- sanitized example configuration
- research and architecture documentation

## Removed from public copy

- `.git/` and all historical commits
- compiled `bin/` output
- third-party JAR binaries
- historical SQL dumps/backups
- SMS/MMS payload files
- real/legacy image assets
- trained `.model` files
- `.train` datasets
- runtime SMS server configuration
- logs and lock files

## Rule

If a file cannot be demonstrated to be public-safe and necessary for understanding the research implementation, it stays out of the public repository.
