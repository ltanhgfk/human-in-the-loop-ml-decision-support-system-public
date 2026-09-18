# Human-in-the-Loop ML Decision Support System

> Historical research and software project for agricultural question classification and expert-assisted decision support through SMS/MMS.

## Overview

This repository preserves the source-level implementation of a Master's research project in Information Systems. The system combines Vietnamese text processing, domain-specific feature engineering, Support Vector Machine (SVM) classification, expert routing, human verification, similarity-based retrieval, and retraining.

The central design principle is **human-in-the-loop decision support**: machine learning assists coordinators and domain experts; it does not replace them.

## Research context

The work was developed as a semi-automatic agricultural advisory system. The research explicitly treated the semi-automatic system as an initial stage toward a more automated advisory system, with real operational data intended to support later development.

Academic publication:

**Lương Thế Anh, Nguyễn Thái Nghe, Nguyễn Chí Ngôn (2014). _Xây dựng hệ thống hỗ trợ khuyến nông trên cây lúa qua mạng thông tin di động_. Tạp chí Khoa học Trường Đại học Cần Thơ, 33, 9–21.**

## System workflow

```text
SMS/MMS
   │
   ├── Vietnamese word segmentation
   │
   ├── Stop-word removal
   │
   └── Keyword / feature construction
              │
              ▼
        Sparse vector
              │
              ▼
       Linear-kernel SVM
              │
              ▼
      Topic classification
              │
       ┌──────┴──────┐
       │             │
       ▼             ▼
 Human verification  Similarity-based retrieval
       │             │
       └──────┬──────┘
              ▼
         Expert routing
              │
              ▼
        Expert response
              │
              ▼
      Human-labelled data
              │
              ▼
          Retraining
```

See [`docs/ML_PIPELINE.md`](docs/ML_PIPELINE.md) for implementation-level details and terminology.

## Main research/technical components

- Vietnamese word segmentation and stop-word processing
- Domain-specific keyword construction
- Sparse bag-of-words feature representation
- Linear-kernel SVM training and prediction
- Multi-class agricultural topic classification
- Separate machine and human classification states
- Expert-to-topic routing
- Human-assisted model retraining
- Term-frequency cosine similarity for retrieval of previously answered questions
- Java backend + JSP/Servlet web application + MySQL persistence

## What this repository does **not** claim

This is a historical ML system. It is **not** presented as a modern deep-learning, transformer, LLM, or RAG implementation.

The original system supported MMS/image handling, but automatic image classification was identified as future work rather than a demonstrated component of the thesis implementation. Likewise, the similarity component uses term-frequency vectors and cosine similarity; it is not semantic embedding search or RAG.

## Reported evaluation

The public repository preserves a dated cross-validation summary from the original project:

- **10-fold cross-validation accuracy: 68.35%**
- Recorded training timestamp: **2014-03-06 13:22:32**

This figure should be interpreted as historical research evidence, not as a modern benchmark. The original training data and trained models are intentionally not included in this public release.

## Repository structure

```text
RSSAPP/                         Java backend and ML/runtime code
RSSWEB/                         JSP/Servlet web portal
config/                         Safe example runtime configuration
data/                           Public data documentation only
database/                       Sanitized schema + fictional sample data
docs/                           Architecture, ML, build, privacy, history
PUBLIC_REPOSITORY_MANIFEST.md   Public-release audit manifest
```

## Public-release policy

The public version deliberately excludes:

- real or historical user records
- phone numbers, addresses, emails, IDs, or credentials
- SMS/MMS payloads and private images
- historical database dumps/backups
- trained models and private training datasets
- compiled binaries and dependency JARs
- the original private Git history

Only fictional demonstration records are included in `database/sample_data.sql`.

See:

- [`docs/DATA_PRIVACY.md`](docs/DATA_PRIVACY.md)
- [`docs/BUILD.md`](docs/BUILD.md)
- [`docs/KNOWN_LIMITATIONS.md`](docs/KNOWN_LIMITATIONS.md)
- [`docs/DEPENDENCIES.md`](docs/DEPENDENCIES.md)
- [`docs/MY_CONTRIBUTION.md`](docs/MY_CONTRIBUTION.md)

## Reproduction

This codebase targets legacy Java/web/SMS/MMS infrastructure and does not contain a modern Maven/Gradle build. A complete historical deployment may require compatible third-party libraries, database setup, and SMS/MMS gateway hardware or services.

The public release is therefore best understood as a **research/software archive and source-level reconstruction**, not a one-command reproducible benchmark.

## Historical significance

The research provides an early example of a practical workflow in which classical machine learning is embedded inside a larger information system and combined with human validation and expert knowledge. The public release preserves that architecture without retroactively attributing modern AI techniques to the original work.
