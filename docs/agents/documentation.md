# Documentation and site

Keep `README.md` and `docs/` aligned with new features and API changes.

- Build the hermetic site output: `bazel build //website:site`.
- Build and copy the site to `reports/site`: `tools/site.sh build`.
- Check generated files and fragments after building: `tools/site.sh check`.
- Preview locally: `tools/site.sh serve`. Rebuild and refresh manually after edits.

For site structure and document registration, read [Website](../development_process.md#website).
