# T01 — Simplify the branch and developer guide

- Status: `pending`
- Blocked by: `None`
- Spec coverage: `R1`, `R2`, `R3`, `AC1`, `AC2`, `AC3`

## Delivers

Actionable developer guidance and a branch-wide simplify pass, validated and published to the existing PR with completed closeout.

## Acceptance criteria

- [ ] Rewrite the development guide around developer actions while retaining required constraints and linked anchors.
- [ ] Review semantic branch changes and mechanical source/fixture changes; correct stale names, comments, visibility, links, and redundant work without changing behavior.
- [ ] Run focused and full checks and record evidence for implementation review and closeout.

## Validation

- Review final prose against existing commands/configuration and verify README anchors.
- Run affected processor golden/nullability tests and Python syntax/artifact checks.
- Run `tools/check.sh` for formatting, build, tests, coverage, hermeticity, artifacts, and site links.
- Compare final generated-code goldens and inspect the complete correction diff.
- At workflow closeout, verify the closeout commit, clean worktree, preserved Proton override, and published CI status.

## Evidence

- `pending`
