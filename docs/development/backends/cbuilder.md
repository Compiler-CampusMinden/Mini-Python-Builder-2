# CBuilder

The CBuilder is specifically written
in order to be used as a backend in a Mini-Python compiler
(i.e. combined together with a Mini-Python frontend,
it forms a Mini-Python compiler).
As the name implies,
the CBuilder generates C code.
This code is dependent on a runtime library,
the so called `c-runtime`.
The API of the CBuilder is fully Mini-Python specific,
that is,
it is modelled after the syntax constructs of the Mini-Python language.
The CBuilder only deals with generating the code,
and therefore requires the user to manually compile and execute the code,
as well as provide a C toolchain capable of compiling the code.
