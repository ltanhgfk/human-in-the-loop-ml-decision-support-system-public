# ML / NLP Pipeline

This document describes the pipeline **exactly as implemented**, with no
proposed or implied changes to the algorithm. See "Future Work" in
`README.md` for ideas that are explicitly *not* implemented.

```
Vietnamese SMS/MMS text (raw message from a farmer)
        │
        ▼
[1] Tokenization + stop-word removal — libsvm/classify/StopWords.java
    - Word segmentation via the vn.hus.nlp.tokenizer library
    - Stop-words removed using stopwordlist.txt
        │
        ▼
[2] Feature / vector representation — libsvm/classify/Vectorize.java
    - Bag-of-words: presence of each dictionary term (keywordlist.txt, or
      autokeywordlist.txt when auto-keyword mode is on) becomes one sparse
      feature; output is a LIBSVM-format sparse vector string
        │
        ▼
[3] Linear-kernel SVM — libsvm/classify/CreateModel.java + ClassifySms.java
    - Training: CreateModel.createTrainingFile() + trainLinearKernel(),
      using the LIBSVM Java port (ca.uwo.csd.ai.nlp), evaluated with
      k-fold cross-validation (SVMTrainer.doCrossValidation)
    - Prediction: ClassifySms.classify() loads the trained model and
      predicts a topic label for a new message's vector
        │
        ▼
[4] Human verification / expert routing — org/smslib/smsserver/MmsMain.java
    - The predicted label determines which expert (registered for that
      topic via tbl_expertmajor) the question is routed to
    - The web portal (Admin_ClassifyMMS.jsp) lets an admin confirm or
      correct the machine's label — this human-confirmed label
      (classifiedByHuman / majoridByHuman) becomes higher-quality future
      training data
        │
        ▼
[5] Retraining — org/smslib/smsserver/Retrain.java (and the alternate
    scheduling design in ProcessModelsThread.java)
    - Periodically pulls messages not yet used for training (used=false),
      optionally rebuilds the keyword dictionary from newly-seen
      vocabulary, and re-runs step [3]'s training/cross-validation
        │
        ▼
[6] Cosine-similarity retrieval / auto-answer — utils/SimilarityUtil.java
    - Independent of the SVM classifier. Re-tokenizes the new question and
      every previously *answered* message using the same StopWords
      pipeline as step [1], builds a term-frequency vector for each, and
      computes cosine similarity: dot(v1,v2) / (‖v1‖ · ‖v2‖)
    - If the best match's similarity meets a configurable threshold
      (tbl_config: auto_answer_threshold, default 75%), that past answer
      is reused automatically instead of waiting on a human expert
    - See docs/HISTORY.md for this component's provenance — it implements
      a term-frequency / bag-of-words cosine similarity, not a semantic
      embedding, and is not an LLM or RAG system
```

## Precise terminology (do not substitute)

| What it is | What it is NOT |
|---|---|
| Linear-kernel Support Vector Machine (SVM), trained on sparse bag-of-words vectors | A neural network / deep learning classifier |
| Bag-of-words / term-frequency feature representation | A learned embedding (word2vec, BERT, sentence-transformers, etc.) |
| Cosine similarity over term-frequency vectors, for retrieval of a previously-answered question | Semantic embedding similarity, an LLM, or a Retrieval-Augmented Generation (RAG) system |
| Vietnamese word-segmentation tokenizer (`vn.hus.nlp.tokenizer`) | A transformer-based tokenizer |
| Human-in-the-loop label correction feeding scheduled batch retraining | Real-time online learning or reinforcement learning |

These distinctions matter for describing this project accurately (e.g. in
`docs/MY_CONTRIBUTION.md` or a CV) — the system's actual sophistication is
in the end-to-end pipeline design and the human-in-the-loop feedback loop,
not in the individual algorithms, which are intentionally simple,
classical, and well-understood 2013–2018-era techniques.

## Reported accuracy (found in-repo, safe to publish)

`RSSWEB/files/accuracy.txt` (a small, non-sensitive text file — a
cross-validation summary, not user data) records:

> 10-fold cross-validation: **68.35%**, last trained: 2014-03-06 13:22:32

This is a real, dated result from the original system (consistent with
the 2014 thesis period), naming the exact cross-validation procedure
(`SVMTrainer.doCrossValidation`, see above) that produced it. This
resolves an open question from the Phase 1 audit's `REVIEW_REQUIRED.md`
("no persisted accuracy figure was found") — one was found during Phase 2
and is safe to cite, since it contains no personal data, only a metric
and a timestamp.

## What is genuinely two separate decision mechanisms

It's worth being explicit that steps [3] (SVM classification) and [6]
(cosine-similarity retrieval) answer different questions and were
combined, not merged into one model:

- **Classification** answers "which of N fixed topics is this about?" —
  used for expert routing.
- **Similarity retrieval** answers "has this almost-exact question been
  answered before?" — used to skip the expert loop for repeat questions.

Both currently share the same text-preprocessing step (tokenization +
stop-word removal), which is why `StopWords.java` is a dependency of both
`Vectorize.java` and `SimilarityUtil.java`.
