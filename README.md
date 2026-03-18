# AI-Powered Code Review Tool

A command-line tool that automates code review for Java projects using LLM APIs, designed for integration into developer workflows and CI pipelines.

## Overview

Traditional code review is time-consuming and inconsistent. This tool uses LLMs to generate structured feedback on bugs, performance issues, and style violations, while an incremental pipeline ensures only modified code is analyzed.

## Key Features

- Incremental analysis using git diff to process only modified files
- File-hashing cache that reduced API calls by 60%
- Structured feedback organized by severity: bugs, performance, style
- Designed for seamless integration into CI pipelines

## Tech Stack

- Java
- OpenAI API
- Git

## Architecture
```
git diff output
      |
      v
Parse changed files
      |
      v
Compute file hash -> Check local cache
      |                    |
   not cached           cached -> return stored feedback instantly
      |
      v
Send to LLM API
      |
      v
Parse response -> categorize by severity
      |
      v
Output structured review report
```

## Core Design Decisions

**Why incremental analysis?**
Sending entire codebases to LLMs on every review is expensive and slow. By diffing against the last reviewed commit, only changed files are processed.

**Why file hashing for cache?**
File content hash is more reliable than timestamps. If a file is reverted to a previous state, the cache correctly returns the old review without an unnecessary API call.
