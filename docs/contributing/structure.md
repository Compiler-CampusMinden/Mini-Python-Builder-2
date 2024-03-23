# Project Structure

- multi-language repo
- difficult to integrate in a common build-system: each component in its own subfolder
- integrated (where necessary, e.g. to build artifacts) via top-level makefile
- c-runtime: c code, custom makefile
- builder: java, gradle
- builder-examples: java, gradle - showcase of builder api (& tests that need to/can be executed manually)
