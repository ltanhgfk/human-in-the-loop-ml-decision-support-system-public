# Dependencies & Code Provenance

Classification legend: **OWN CODE** (written/modified by the project
owner for this system), **THIRD-PARTY** (an external library, vendored as
source or binary), **UNCERTAIN** (plausibly modified third-party code;
not verified line-by-line in this audit).

| Library / package | Location | Purpose | Classification | License | Vendored as |
|---|---|---|---|---|---|
| SMSLib (core) | `RSSAPP/src/org/smslib/**` (excl. `smsserver`) | GSM modem/AT-command driver, SMS gateway abstraction | THIRD-PARTY | GPL (per upstream project) — **license requires verification** against the exact bundled version | source |
| SMSLib "smsserver" sample app | `RSSAPP/src/org/smslib/smsserver/{SMSServer,GetSMSThread,ProcessMessagesThread,RunApplicationsThread,gateways/*}.java` | Bundled sample daemon that ships with SMSLib | **UNCERTAIN** — plausibly a mix of unmodified SMSLib sample code and project-owner modifications; not diffed line-by-line against a specific SMSLib release in this audit | Same as SMSLib — **license requires verification** | source |
| `MmsMain.java`, `MmsMainMultiNetwork.java`, `MmsMain_Old_Chua nang cap Cosine.java`, `Retrain.java`, `ProcessModelsThread.java` | same directory as above | Message orchestration + retraining, built on top of the SMSLib sample app | **OWN CODE** (these implement this project's specific business logic — classification routing, retraining — not present in a generic SMSLib sample) | N/A (see `docs/HISTORY.md` for the note on `MmsMain.java`'s 2026 modification) | source |
| JWAP | `RSSAPP/src/net/sourceforge/jwap/**` | WAP/WBXML client | THIRD-PARTY | Unverified — **license requires verification** | source |
| ajwcc pduUtils | `RSSAPP/src/org/ajwcc/pduUtils/**` | GSM PDU (SMS) encode/decode | THIRD-PARTY | Unverified — **license requires verification** | source |
| JKU MMS library | `RSSAPP/src/at/jku/soft/mms/**` | MMS PDU parsing/building | THIRD-PARTY | Unverified — **license requires verification** | source |
| `mmslib.mms.*` (MmsGetter, MMSSender, GatewayConfig, HexStringConverter) | `RSSAPP/src/mmslib/mms/**` | Integration layer between the JKU MMS library and this project | **UNCERTAIN** — likely project-owner integration code built on top of the JKU library's API, but not confirmed to contain zero copied JKU sample code | N/A | source |
| LIBSVM (Java port) | `RSSAPP/src/ca/uwo/csd/ai/nlp/**` | SVM solver, kernels, `SparseVector`, `Tree` | THIRD-PARTY (University of Western Ontario's Java port of the original C++ LIBSVM by Chih-Chung Chang & Chih-Jen Lin) | Unverified for this specific port — **license requires verification**; original LIBSVM is BSD-style | source |
| `libsvm.classify.*` (StopWords, Vectorize, CreateModel, ClassifySms) | `RSSAPP/src/libsvm/classify/**` | NLP preprocessing + training/prediction pipeline calling into the LIBSVM port above | **OWN CODE** | N/A | source |
| `utils.SimilarityUtil` | `RSSAPP/src/utils/SimilarityUtil.java` | Cosine-similarity retrieval for auto-answering | **OWN CODE** (2026 reconstruction of a reported 2016–2018 personal extension — see `docs/HISTORY.md`; not third-party, not an AI-original contribution) | N/A | source |
| Vietnamese tokenizer (`vn.hus.nlp.tokenizer`) | jar in `RSSAPP/libs/` + model data in `RSSAPP/models/` | Vietnamese word segmentation, used by `StopWords.java` | THIRD-PARTY | Unverified — **license requires verification** (likely JVnTextPro/vnTokenizer, VLSP project lineage, not confirmed against the exact bundled jar) | binary (jar) + model data |
| MySQL Connector/J | `.jar` in `RSSAPP/libs/`, `RSSWEB/WebContent/WEB-INF/lib/` | JDBC driver | THIRD-PARTY | GPL/commercial dual license (Oracle) — standard, well-known | binary |
| Apache log4j | `.jar` in `RSSAPP/libs/` | Logging | THIRD-PARTY | Apache License 2.0 (standard, well-known for the log4j 1.x line used here) | binary |
| JUnit, Trove, JDOM | `.jar` in `RSSAPP/libs/` | Testing / collections / XML | THIRD-PARTY | Various standard OSS licenses — not individually re-verified against the exact bundled versions | binary |
| JSTL / "standard" taglib | `.jar` in `RSSWEB/WebContent/WEB-INF/lib/` | JSP tag library | THIRD-PARTY | Standard OSS (Apache-lineage) — not individually re-verified | binary |
| jQuery, jQuery UI, jQuery Validate, jQuery Tools, md5.js | `RSSWEB/WebContent/js/` | Front-end scripting | THIRD-PARTY | MIT (jQuery/jQuery UI, standard) — not individually re-verified against the exact bundled versions | source (minified/plain JS) |
| `model/*.java`, `dao/DBAction.java`, `util/DbUtil.java` (both `RSSAPP` and `RSSWEB`) | `RSSAPP/src/{model,dao,util}`, `RSSWEB/src/{model,dao,util,global}` | Domain entities + generic JDBC access layer | **OWN CODE** | N/A | source |
| `RSSWEB/src/controller/*.java`, `WebContent/*.jsp` | `RSSWEB/**` | Web MVC layer | **OWN CODE** | N/A | source |

## What this means for publishing

- No third-party **source** was removed automatically in Phase 2, per the
  explicit instruction not to auto-delete third-party code. It remains in
  the repository, classified as above.
- Third-party **binaries** (`.jar` files) and the vendored Vietnamese
  tokenizer's pretrained **model data** (`RSSAPP/models/`) are excluded
  via `.gitignore` (not deleted from the original — see
  `docs/DATA_PRIVACY.md`), since committing binaries is generally poor
  practice and these are trivially re-obtainable from upstream.
- **Every "license requires verification" entry above must be resolved
  before treating this repository as safe for public redistribution
  as-is**, particularly for the SMSLib-derived code, since SMSLib is
  GPL-licensed upstream and GPL has specific redistribution requirements
  that were not evaluated as part of this cleanup (this is a legal
  question, not a code question, and is out of scope for an AI-assisted
  source audit). See `REVIEW_REQUIRED.md`.
- No third-party code's authorship or license was reassigned, claimed, or
  asserted in this cleanup — where uncertain, this document says so
  explicitly rather than guessing.
