# Components

The project consists of 2 major projects:

- the [`c-runtime`](../../c-runtime): implementation of a runtime environment for Mini-Python
- the [`builder`](../../builder): the Builder API, which is responsible for generating the Mini-Python code

Other parts include:

- the [`builder-examples`](../../builder-examples): examples of using the Builder API (also (ab)used for manual testing)
- the [top-level `Makefile`](../../Makefile): runs common tasks (formatting across the whole project, generating images for the docs, building the docs, building docker images, etc)
- the [docker files](../../docker): docker images for not-so-common tool(chain)s (e.g. C-to-WASM cross compilation toolchain)
- the [`docs`](../../docs): documentation of this project
