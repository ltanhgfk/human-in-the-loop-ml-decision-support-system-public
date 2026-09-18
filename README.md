# Human-in-the-Loop ML Decision Support System

A historical research and software project for agricultural question routing through SMS/MMS, combining Vietnamese text processing, domain-specific feature engineering, SVM classification, expert routing, human validation, and model retraining.

## Research context

The system was developed as part of a Master's thesis in Information Systems and was subsequently described in a scientific publication. The research focused on a semi-automatic decision-support workflow in which machine classification assists human coordinators and domain experts rather than replacing them.

### Core workflow

```text
SMS/MMS
   ↓
Vietnamese text preprocessing
   ↓
Keyword / feature construction
   ↓
SVM classification
   ↓
Human verification
   ↓
Expert routing
   ↓
Expert response
   ↓
New labelled data
   ↓
Model retraining
```

## Main ML components

- Vietnamese word segmentation and stop-word processing
- Domain-specific keyword construction
- Sparse feature/vector generation
- SVM training and prediction using LibSVM-compatible code
- Multi-class routing by agricultural topic
- Separate machine and human classification states
- Human-assisted retraining workflow

## Repository scope

This public repository intentionally contains the research/application source and documentation only. The following are excluded:

- real or historical database dumps
- credentials and local configuration files
- SMS/MMS payloads and personal data
- trained models and training datasets derived from historical data
- compiled binaries and vendored third-party JARs
- legacy image assets whose provenance is not established
- Git history containing the original private snapshot

See `docs/DATA_PRIVACY.md` and `docs/BUILD.md` for the public-repository constraints.

## Important limitation

The original research supported MMS/image handling, but automatic image classification was documented as future work. This repository therefore does **not** claim that the original thesis implemented modern multimodal machine learning.

## Research reference

Lương Thế Anh, Nguyễn Thái Nghe, Nguyễn Chí Ngôn (2014). *Xây dựng hệ thống hỗ trợ khuyến nông trên cây lúa qua mạng thông tin di động*. Tạp chí Khoa học Trường Đại học Cần Thơ, 33, 9–21.
