# Project History & Provenance

This document exists specifically to give an accurate, dated account of
*when* different parts of this system were built, because the source
tree contains evidence of at least three distinct time periods. Getting
this right matters for anyone using this repository as evidence of
research work (e.g. a PhD application), so this document is deliberately
conservative: it states what the evidence shows and what the project
owner reports, and does not blur the two together.

## Timeline

### 2014 — Original Master's thesis system

The core system — Vietnamese NLP preprocessing, bag-of-words feature
extraction, linear-kernel SVM classification, the message-routing state
machine, the human-in-the-loop retraining loop, and the JSP/Servlet
admin/expert web portal — was designed and built as part of a Master's
thesis at Can Tho University. File modification timestamps consistent
with this period were found on, among others,
`RSSAPP/src/org/smslib/smsserver/MmsMainMultiNetwork.java` (2014-02-25).

### 2016–2018 — Reported personal extension: cosine-similarity question retrieval

**The project owner reports that a cosine-similarity-based extension was
personally developed around 2016–2018.** The corresponding historical
source code is no longer available. The current repository contains a
reconstruction/integration of this functionality based on the remaining
source and system architecture. It should not be treated as a
byte-for-byte copy of the original historical implementation.

What the source tree independently corroborates: a file named
`RSSAPP/src/org/smslib/smsserver/MmsMain_Old_Chua nang cap Cosine.java`
("MmsMain_Old_Not-yet-upgraded-to-Cosine.java") exists, with a file
modification date of 2018-05-19. It contains a message-routing loop with
**no** cosine-similarity or automatic-answer logic — i.e., it is
consistent with being a snapshot saved *before* a cosine-similarity
upgrade was attempted, which lines up with the project owner's report of
working on such an extension in that period. This is corroborating
evidence for the *timing and existence* of the effort, not a surviving
copy of its implementation.

**What is not claimed:** that `utils/SimilarityUtil.java` currently in
this repository is the original 2016–2018 code; that the exact algorithm,
thresholding approach, or integration points match what was originally
built; or that the 2018-dated file above ever contained a working
cosine-similarity implementation that was later lost. None of that can be
established from the surviving source.

### 2026 — Repository recovery, reconstruction, and integration

The current `utils/SimilarityUtil.java` and its integration into
`org/smslib/smsserver/MmsMain.java` (cosine similarity over term-frequency
vectors, used to auto-answer a new question when a sufficiently similar
past question has already been answered) were produced during 2026, as
part of recovering/reconstructing this reported functionality for
inclusion in this repository, together with the surrounding
documentation, privacy cleanup, and repository organization described
elsewhere in `docs/`.

This 2026 work is accurately described as: **repository recovery,
reconstruction, cleanup, documentation, integration, and testing** — not
as the original invention of the cosine-similarity concept for this
system, and not as new research contributed in 2026. The underlying idea
(retrieve the most similar previously-answered question and reuse its
answer when confident enough) is the project owner's reported 2016–2018
work; the 2026 contribution is re-implementing and integrating that idea
into the present codebase given that the original implementation's source
was not available.

## Summary table

| Period | What | Status |
|---|---|---|
| 2014 | Original thesis: NLP preprocessing, SVM classification, message routing, retraining loop, web portal | Confirmed by surviving source + timestamps |
| 2016–2018 | Cosine-similarity question-retrieval extension | **Reported by project owner.** Original source not available. Indirect corroboration: a 2018-dated pre-upgrade snapshot filename (`MmsMain_Old_Chua nang cap Cosine.java`) exists and contains no cosine code, consistent with (but not proof of) an in-progress upgrade at that time |
| 2026 | Reconstruction of the cosine-similarity extension (`SimilarityUtil.java` + `MmsMain.java` integration) into this repository, plus repository cleanup/documentation | Confirmed by file timestamps and this repository's own change history |

## Why this matters

If you (the project owner) present this repository — for a PhD
application, a CV, or otherwise — as evidence of your research work, the
honest and accurate framing is:

- The 2014 system is your thesis work, directly evidenced by source.
- The cosine-similarity *idea and its original implementation* is your
  reported 2016–2018 personal extension, not a 2026 addition and not an
  AI-generated original contribution.
- The specific `SimilarityUtil.java` code present in this repository
  today is a 2026 reconstruction of that idea, built to fit into the
  recovered codebase — useful for demonstrating the *system design*
  (term-frequency cosine similarity for retrieval-based auto-answering),
  but not a historical artifact in itself.

See `docs/MY_CONTRIBUTION.md` for how this translates into CV/portfolio
language, and `REVIEW_REQUIRED.md` for the open questions this audit
could not resolve about the 2016–2018 period.
