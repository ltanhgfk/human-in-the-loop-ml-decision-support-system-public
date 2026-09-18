# Human-in-the-Loop ML Decision Support System

> Historical research and software project for agricultural question classification and expert-assisted decision support through SMS/MMS.

## Overview

This repository preserves the source-level implementation of a Master's research project in Information Systems.

The system combines Vietnamese text processing, domain-specific feature engineering, Support Vector Machine (SVM) classification, expert routing, human verification, similarity-based retrieval, and retraining.

The central design principle is **human-in-the-loop decision support**: machine learning assists coordinators and domain experts rather than replacing them.

## Research Context

The system was developed as a semi-automatic agricultural advisory system for rice-related questions through mobile information services.

The research investigated how classical machine learning could be integrated into a practical information system while retaining human verification and domain-expert involvement.

### Academic Publication

Lương Thế Anh, Nguyễn Thái Nghe, Nguyễn Chí Ngôn (2014).

**Xây dựng hệ thống hỗ trợ khuyến nông trên cây lúa qua mạng thông tin di động.**

*Tạp chí Khoa học Trường Đại học Cần Thơ*, 33, 9–21.

## System Workflow

```text
SMS/MMS
   │
   ├── Vietnamese word segmentation
   ├── Stop-word removal
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

## Main Components

### Vietnamese Text Processing

The system processes Vietnamese agricultural questions before classification, including word segmentation and stop-word processing.

### Feature Engineering

Domain-specific keywords and sparse bag-of-words representations are used to transform text into machine-learning features.

### Machine Learning

The classification component uses a **Support Vector Machine (SVM)** with a linear kernel for multi-class agricultural topic classification.

### Human-in-the-Loop Decision Support

Machine classification is combined with human verification and domain-expert routing. The system is designed to support human decision-makers rather than operate as a fully autonomous advisory system.

### Similarity-Based Retrieval

Previously answered questions can be retrieved using term-frequency representations and cosine similarity.

This component is a classical information-retrieval mechanism. It is **not** semantic embedding search and is **not** Retrieval-Augmented Generation (RAG).

### Expert Routing

Classified agricultural questions can be routed toward the appropriate domain expert for human response and verification.

### Model Retraining

Human-labelled information can be incorporated into the learning process to support subsequent model retraining.

## Reported Evaluation

The preserved research record reports:

**10-fold cross-validation accuracy: 68.35%**

Recorded training timestamp:

`2014-03-06 13:22:32`

The reported accuracy is retained as historical research evidence from the original project.

It should not be interpreted as a modern benchmark or as a directly comparable result against contemporary machine-learning systems.

## Historical Scope

This repository represents a historical research and software system.

It is **not** presented as a modern deep-learning, transformer, LLM, embedding-search, or RAG implementation.

The system reflects the technology and research design of its original development period, including classical machine learning, sparse text representations, expert involvement, and workflow-based information-system components.

The original system included MMS/image handling. Automatic image classification, however, was identified as future work rather than a demonstrated component of the thesis implementation.

## Technology Stack

The preserved implementation primarily consists of:

- Java
- JSP / Servlet web application
- MySQL
- Vietnamese NLP / text-processing components
- Linear-kernel SVM
- Sparse text feature representation
- Term-frequency cosine similarity
- SMS/MMS-oriented application components

The codebase targets a legacy Java/web environment and does not provide a modern Maven/Gradle one-command build.

## Repository Structure

```text
RSSAPP/       Java backend, NLP, classification and runtime components
RSSWEB/       JSP/Servlet web application
config/       Safe example runtime configuration
data/         Public data documentation
database/     Sanitized database schema and fictional demonstration data
docs/         Research, architecture, build, privacy and limitation documentation
```

## Public Release

This repository is a sanitized public research release.

The public version deliberately excludes:

- real or historical user records
- phone numbers, addresses, email addresses, personal identifiers
- credentials
- private SMS/MMS payloads
- private images
- historical database dumps or backups
- private training datasets
- trained model files
- compiled binaries
- dependency JAR files
- private runtime configuration

Only sanitized or fictional demonstration material is intended to remain in the public repository.

See [`docs/DATA_PRIVACY.md`](docs/DATA_PRIVACY.md) and [`PUBLIC_REPOSITORY_MANIFEST.md`](PUBLIC_REPOSITORY_MANIFEST.md) for details.

## Reproduction

This repository is primarily a **research/software archive and source-level reconstruction**.

A complete historical deployment may require a compatible legacy Java environment, third-party libraries, database configuration, historical runtime configuration, and compatible SMS/MMS gateway infrastructure or services.

The original private training data and trained models are not included in the public release.

Therefore, the repository should not be interpreted as a one-command reproducible benchmark.

See [`docs/BUILD.md`](docs/BUILD.md) for build information.

## Documentation

Additional documentation is available in the `docs/` directory:

- [`docs/ML_PIPELINE.md`](docs/ML_PIPELINE.md) — machine-learning pipeline and terminology
- [`docs/BUILD.md`](docs/BUILD.md) — build and environment information
- [`docs/DATA_PRIVACY.md`](docs/DATA_PRIVACY.md) — public-release and privacy policy
- [`docs/KNOWN_LIMITATIONS.md`](docs/KNOWN_LIMITATIONS.md) — known technical and research limitations
- [`docs/DEPENDENCIES.md`](docs/DEPENDENCIES.md) — dependency information
- [`docs/MY_CONTRIBUTION.md`](docs/MY_CONTRIBUTION.md) — contribution and research context

## Research Contribution

The project demonstrates the integration of:

- classical machine learning
- Vietnamese text processing
- domain-specific feature engineering
- information retrieval
- human verification
- expert knowledge
- model retraining
- decision-support workflow

The main research perspective is the integration of machine learning into a larger information system while maintaining a **human-in-the-loop** process for verification, expert involvement, and decision support.

## Limitations

1. The system is based on classical machine-learning methods and legacy software infrastructure.
2. The original training data and trained models are not included in the public release.
3. The reported evaluation represents the historical research environment and dataset.
4. The public repository is not intended to reproduce the complete historical production environment.
5. Similarity retrieval is based on term-frequency vectors and cosine similarity rather than modern semantic embeddings.
6. The system should not be interpreted as an autonomous agricultural advisory system.

## Historical Research Note

This repository is preserved as a research and software artifact.

The objective of the public release is to make the software architecture, machine-learning approach, research context, and human-in-the-loop design inspectable while avoiding the publication of private or sensitive historical data.

The project therefore preserves the distinction between:

**machine prediction → human verification → expert decision support**

rather than retroactively describing the original system using modern AI terminology.

---

**Research area:** Information Systems · Machine Learning · Natural Language Processing · Decision Support Systems · Human-in-the-Loop AI
