<!--
SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors

SPDX-License-Identifier: CC-BY-SA-4.0

Mini-Python Builder documentation © 2024 by Mini-Python Builder Contributors is licensed under CC BY-SA 4.0.

-->

# MLIR

[MLIR](https://mlir.llvm.org/)[^mlir-citation] could be described
as kind of a "meta" IR:
It describes a generic IR format,
which is based on the idea of so called "dialects".
Those allow to extend the IR with programming language, hardware
or programming idiom specific structures.
Another important aspect of MLIR is the ability
to describe generic optimisations on the IR
and transformations between different dialects.

MLIR is part of the [LLVM Compiler Project](https://llvm.org),
and as such is written in C++.
Thus, new dialects or transformations must consequently also
be implemented in C++.
To ease the implementation of new transformations,
it also offers a declarative language,
called
[TableGen](https://mlir.llvm.org/docs/DefiningDialects/Operations/),
which is used to generate the corresponding C++ code at build time.

Even though the implementation of dialects is limited to C++,
MLIR offers [bindings](https://mlir.llvm.org/docs/Bindings/)
to other programming languages,
currently those supported are Python and C.

[^mlir-citation]: Chris Lattner, Mehdi Amini, Uday Bondhugula, Albert Cohen, Andy Davis, Jacques Pienaar, River Riddle, Tatiana Shpeisman, Nicolas Vasilache, and Oleksandr Zinenko. “MLIR: Scaling compiler infrastructure for domain specific computation.” In 2021 IEEE/ACM International Symposium on Code Generation and Optimization (CGO), pp. 2-14. IEEE, 2021 (DOI 10.1109/CGO51591.2021.9370308, https://ieeexplore.ieee.org/abstract/document/9370308)
