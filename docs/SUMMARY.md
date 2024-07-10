<!--
SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors

SPDX-License-Identifier: CC-BY-SA-4.0

Mini-Python Builder documentation © 2024 by Mini-Python Builder Contributors is licensed under CC BY-SA 4.0.

-->

# Summary

[Overview (en)](readme.en.link.md)
[Übersicht (de)](readme.md)

# Verwendung (de)

- [Übersicht des Builders](builder.md)
- [Setup](setup.md)
- [Verwendung des generischen Builders](usage_generic_builder.md)
- [Verwendung des WASM Builders](usage_wasm_builder.md)
- [Verwendung des CBuilders](usage_cbuilder.md)
- [Verwendung des generierten C-Codes](usage_generated_code.md)

# Mini-Python (de)

- [Syntaktische Definition](syntax_definition.md)
- [Semantische Definition](semantic_definition.md)

-----------

# Architecture

- [Introduction](architecture/readme.md)
- [Components](./architecture/components.md)
- [Terms](architecture/terms.md)

# Builder

- [Public API](architecture/builder/public_api.md)
- [Transformation/Lowering](architecture/builder/transformations_lowering.md)

# Backends

- [c-runtime](./architecture/backends/c-runtime.md)
- [wasm](./architecture/backends/wasm.md)

-----------

# Contributing

- [Project Structure](./contributing/structure.md)

-----------

# Development

- [Introduction](development/readme.md)

## Program-Builder API

- [Approaches to Program-Builder APIs](development/builder_api/approaches.md)
    - [Backend-Specific Java API](development/builder_api/backend_specific.md)
    - [MLIR](development/builder_api/mlir.md)
    - [Backend-Agnostic Java API](development/builder_api/backend_agnostic.md)
- [Comparison](development/builder_api/comparison.md)

## Compiler-Backends

- [Options]()
    - [MLIR](development/backends/mlir.md)
    - [WASM](development/backends/wasm.md)
    - [Custom Made](development/backends/custom.md)
    - [Existing C-backend](development/backends/cbuilder.md)
- [Comparison](development/backends/comparison.md)


-----------

[License](license.link.md)
