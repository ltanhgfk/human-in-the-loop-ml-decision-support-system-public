# System Architecture

## Two applications, one database

- **`RSSAPP`** — Java backend. Polls a MySQL database in a continuous
  loop, integrates with a GSM modem (via SMSLib) for SMS/MMS transport,
  runs the ML pipeline (see `docs/ML_PIPELINE.md`), and orchestrates the
  message lifecycle.
- **`RSSWEB`** — JSP/Servlet web portal (no framework), used by admins
  and agricultural experts to configure the system, review/correct
  machine classifications, and answer routed questions.

They share a MySQL database (`assmms`) and a near-identical copy of the
`model`/`dao`/`util` data-access code (developed as two separate Eclipse
projects rather than a shared library — see `REVIEW_REQUIRED.md`).

## Message lifecycle (state machine)

Driven from `RSSAPP/src/org/smslib/smsserver/MmsMain.java`, using boolean
flags on each `tbl_mms` row:

| Flag | Meaning |
|---|---|
| `classifiedByMachine` | SVM has assigned a topic |
| `classifiedByHuman` | An admin/expert has confirmed or corrected the topic |
| `answered` | A reply exists (from an expert, or from the similarity-based auto-answer) |
| `sentexpert` | The question has been forwarded to an expert |
| `sentfarmer` | The answer has been sent back to the farmer |
| `used` | This message has already been folded into a training run |

Each cycle of `MmsMain.run()`:

1. **Classify pass** — spam/blacklist check, then SVM classification for
   everything else, then (per the reconstructed 2018 extension — see
   `docs/HISTORY.md`) a cosine-similarity check against previously
   answered questions, which can resolve the message immediately without
   an expert.
2. **Routing pass** — forwards classified-but-unanswered messages to a
   matching expert.
3. **Delivery pass** — sends answered-but-undelivered messages back to
   the farmer.

## Retraining loop

`Retrain.java` (standalone, timer-driven) and `ProcessModelsThread.java`
(an alternate design: a `Thread` embedded in the server process, gated on
message-volume thresholds) both periodically pull newly human-verified
messages, rebuild the keyword dictionary, and retrain the SVM model —
closing the human-in-the-loop feedback cycle described in
`docs/ML_PIPELINE.md`.

## Technology stack

| Layer | Technology |
|---|---|
| Language | Java (`javax.servlet`-era; pre-Java-7-idiomatic in places) |
| ML | LIBSVM (Java port, `ca.uwo.csd.ai.nlp`), linear kernel |
| NLP | `vn.hus.nlp.tokenizer` (Vietnamese word segmentation) + custom stop-word list |
| SMS/MMS transport | SMSLib (GSM AT-command modem driver), `at.jku.soft.mms` + `mmslib.mms` (MMS PDU), `org.ajwcc.pduUtils` (SMS PDU), JWAP (WAP client) |
| Database | MySQL |
| Web | JSP + hand-rolled Servlets, jQuery front end |
| Build | Eclipse-managed classpath (no Maven/Gradle/Ant build file present) |

See `docs/DEPENDENCIES.md` for which parts of this stack are vendored
third-party code versus original work, and `docs/BUILD.md` for what a
full build would require.
