# Public Release Checklist

- [x] Remove original private Git history
- [x] Remove runtime credentials and private configuration
- [x] Remove historical database dumps and private records
- [x] Remove SMS/MMS payloads and private images
- [x] Remove trained models and historical training data
- [x] Remove compiled binaries and vendored dependency JARs
- [x] Add example runtime configuration
- [x] Add fictional database sample data
- [x] Document ML pipeline
- [x] Document architecture
- [x] Document privacy constraints
- [x] Document dependencies and provenance
- [x] Document known limitations
- [x] Document research contribution
- [x] Record historical evaluation result with date
- [x] Scan public tree for obvious credential/PII artifacts
- [x] Verify Git history contains only the public-release commit

## Before publishing

- [ ] Review the repository once more in the GitHub web UI after upload.
- [ ] Confirm no newly generated files, IDE metadata, credentials, or local data were added.
- [ ] Confirm third-party dependency licenses before redistributing any third-party source.
