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

- [Introduction]()
- [Components](./architecture/components.md)

# Builder

- [Public API]()
- [Transformation/Lowering](architecture/builder/transformations_lowering.md)

# Backends

- [c-runtime](./architecture/backends/c-runtime.md)
- [wasm](./architecture/backends/wasm.md)

-----------

# Contributing

- [Project Structure](./contributing/structure.md)

-----------

# Development

- [Introduction]()

## Program-Builder API

- [Features]()
- [Approaches to Program-Builder APIs]()
    - [Backend-Specific Java API]() <!-- the least specific one: both frontend and backend specific -->
    - [MLIR]() <!-- the most generic approach discussed here: neither frontend nor backend specific -->
    - [Backend-Agnostic Java API]() <!-- a middle ground: generic backend intended -->
- [Comparison]()

## Compiler-Backends

- [Options]()
    - [MLIR](development/backends/mlir.md)
    - [WAMS](development/backends/wasm.md)
    - [Custom Made]()
    - [Existing C-backend]()
- [Comparison](development/backends/comparison.md)


-----------

[License](license.link.md)
