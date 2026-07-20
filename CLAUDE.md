# CLAUDE.md

## Project Identity
Assorted Tweaks and Fixes (short ATNF) is a kitchen sink mod which is implementing compatibility between two or more mods, but also temporary fixes. Those are gated by a conditional mixin plugin system and are only applied when all mods it targets/uses are loaded within the specified version range of those mods. It also adds some convienient general tweaks and featurer, which are generally not conditionally gated - or stripped out content from another mod, which are gated in reverse eg. when the mod the feature was stripped from is not loaded.

## Working with Claude
Avoid writing into the `/home/.claude` home directory (Claude Code's own system/session storage). The user prefers persistent notes to live in this project's own `./.claude/` directory instead.
