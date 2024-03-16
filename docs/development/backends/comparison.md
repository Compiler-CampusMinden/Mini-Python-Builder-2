<!--
SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors

SPDX-License-Identifier: CC-BY-SA-4.0

Mini-Python Builder documentation © 2024 by Mini-Python Builder Contributors is licensed under CC BY-SA 4.0.

-->

# Comparison

| Backend           | Development Effort | 3rd-party Dependencies | Builder generates Executable Code? |
|-------------------|--------------------|------------------------|------------------------------------|
| [MLIR](mlir.md) | MiniPython Dialect, Lowering MiniPython Dialect to e.g. LLVM IR, JNI Integration | LLVM (MLIR) | ✅                                 |
| [WebAssembly]() | Java API for emitting WASM, MiniPython runtime support in WASM | WebAssembly Runtime (+ WASI Implementation for that Runtime) | ✅ (JNI even allows execution in builder) |
| [Custom Bytecode]() | Bytecode Format, Java API for emitting Bytecode, Bytecode Runtime | ❔ | ❔|
| [QBE]() | Java API for emitting QBE IR, MiniPython runtime linkable with assembly | QBE, assembly compiler | ❌                                 |
