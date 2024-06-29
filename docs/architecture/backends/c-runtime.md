<!--
SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors

SPDX-License-Identifier: CC-BY-SA-4.0

Mini-Python Builder documentation © 2024 by Mini-Python Builder Contributors is licensed under CC BY-SA 4.0.

-->

# Backends: c-runtime

The CBuilder backend generates C code,
using C for control structures and function calls,
and the `c-runtime` to express all Python concepts
that go beyond what standard C can provide
(classes, object orientation, etc).

The `c-runtime` is structured in a way that allows
to write the generated code into the `program.c` file
and run `make` afterwards,
to build an executable of the translated Python code.

The c-runtime originally was only used for the C-Builder backend.
Currently it is additionally used as a library for the WebAssembly backend.
For more information on the WebAssembly backend see [Backends: WASM](wasm.md).


## Builder-API and Code Generation Relationship

The builder's internal API is centered around the idea
that all aspects of Python can be built into an equivalent
string of C-code.
Of course, things like functions play multiple roles,
they are defined once, but referred to many times
by other parts of the program.
On top of that, function calls are simply evaluated
when used as a statement (i.e. `print("Hello World")`)
but can also be used as an expression that yields a value
(i.e. `print(greet("World"))`).
These concepts are expressed with the `Statement`
and `Expression` interfaces.

The main entry point of a CBuilder-based Python translation
is the `Program` class.

To reference built-in or declared functions, variables and classes,
the CBuilder introduces a `Reference` object.
This approach relies on the scoping rules of the C language,
that is declarations exist as variables in the final C program,
and a `Reference` expands to the name of this variable, on the C language level.

The short-circuiting boolean operators (or, and) are also
implemented by using the semantic already expressable in C.
They use the `||` and `&&` operators respectively,
though the mini-python objects need to be unwrapped
into a C boolean beforehand and vice-versa afterwards.

