# Bashpile

It's the greatest!

## Differences from previous version

A rewrite from scratch was justified for a few reasons:

1. To simplify deployment (no dog-fooding for it's own sake) we use Kotlin with a Native Target
   2. This also means no JVM required for end-users
2. A Bashpile AST to avoid Regex string munging
   1. Before context only bubbled up from below in the form of Translation metadata and created a string
   2. When additional context was discovered above the Translation string was edited directly, sometimes using complex regex's
   3. Now an AST is generated that can be modified from above easily and only rendered as Bashpile text after all context is discovered
3. A simplified Translation object (e.g. no preambles)