# Components

- Builder, Mini-Python runtime / c-runtime
- Public API: Builder
- Builder embeds `c-runtime` *source*; at runtime depends on C compilation toolchain being available
- Builder emits C code that uses `c-runtime`'s API
