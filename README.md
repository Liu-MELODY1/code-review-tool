# AI-Powered Code Review Tool

A command-line tool that automates code review for Java projects using LLM APIs, designed for integration into developer workflows and CI pipelines.

## Overview

Traditional code review is time-consuming and inconsistent. This tool uses LLMs to generate structured feedback on bugs, performance issues, and style violations, while an incremental pipeline ensures only modified code is analyzed — keeping costs low and feedback fast.

## Key Features

- Incremental analysis using `git diff` to process only modified files
- File-hashing cache (SHA-256) that reduced API calls by 60%
- Structured feedback organized by severity: bugs, performance, style
- Disk-backed cache so reviews persist between runs
- Designed for seamless integration into CI pipelines

## Tech Stack

- Java
- OpenAI API (GPT-4)
- Git

## Project Structure
```
code-review-tool/
├── src/
│   ├── CodeReviewer.java      # Main entry point and review orchestration
│   ├── GitDiffParser.java     # Parses git diff output to find changed files
│   ├── HashUtil.java          # SHA-256 hashing for cache key generation
│   ├── LLMClient.java         # OpenAI API integration
│   ├── ReviewResult.java      # Data model for structured review output
│   └── CacheManager.java      # Disk-backed cache to avoid redundant API calls
└── README.md
```

## Architecture
```
git diff output
      |
      v
Parse changed .java files
      |
      v
Compute SHA-256 hash per file
      |
      v
Check local disk cache
      |              |
  not cached       cached
      |              |
      v              v
Send to LLM     Return stored
    API         feedback instantly
      |
      v
Parse JSON response
      |
      v
Categorize by severity (bugs / performance / style)
      |
      v
Print structured review report
```

## Core Design Decisions

**Why incremental analysis?**

Sending entire codebases to LLMs on every review is expensive and slow. By using `git diff`, only files changed since the last commit are processed. This makes the tool practical for real developer workflows where reviews run frequently.

**Why file hashing for cache?**

File content hash (SHA-256) is more reliable than timestamps. If a file is reverted to a previous state, the cache correctly returns the prior review without triggering an unnecessary API call. The cache is also persisted to disk so it survives between runs.

**Why structured JSON output from the LLM?**

Prompting the model to return a consistent JSON schema with `bugs`, `performance`, and `style` arrays makes the output programmatically parseable. Each issue includes a line number and description, enabling future integration with IDE plugins or GitHub Actions annotations.

## How to Run
```bash
# Clone the repo
git clone https://github.com/Liu-MELODY1/code-review-tool

# Set your OpenAI API key
export OPENAI_API_KEY=your_key_here

# Compile
javac src/*.java

# Run on a Java project
java CodeReviewer /path/to/your/project
```

## Example Output
```
Reviewing changed files...

File: src/PaymentService.java
=== BUGS ===
[HIGH] Line 42: Null check missing before calling user.getAccount() — will throw NullPointerException if user is not authenticated

=== PERFORMANCE ===
[MEDIUM] Line 87: Database query inside loop — consider batching queries outside the iteration

=== STYLE ===
[LOW] Line 103: Method name 'processP' is not descriptive — consider renaming to 'processPayment'

Total issues found: 3
Cache hit rate: 60% (3 files skipped, 2 files reviewed)
```

## Testing

Unit tests cover three core components:

- **HashUtil**: determinism, uniqueness, null safety
- **CacheManager**: cache miss, cache hit, persistence, clear
- **ReviewResult**: empty state, list initialization
```bash
# Run tests
javac -cp junit.jar src/*.java test/*.java
java -cp .:junit.jar org.junit.runner.JUnitCore CodeReviewerTest
```

## Future Improvements

- GitHub Actions integration for automated PR review
- Support for Python and TypeScript in addition to Java
- Configurable severity thresholds to fail CI on HIGH issues
- Web dashboard to track review history over time
