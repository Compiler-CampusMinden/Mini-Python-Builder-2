# Custom Backends

Since a custom backend is very specific to the compiler toolchain it belongs to,
this section does not go into detail on a specific backend,
but rather focuses on the merits and/or disadvantages of developing
and using a custom backend.

As an example, the Zig language noted
that switching to a self-hosted implementation
allows to more closely integrate the compiler implementation with the requirments
of the language [^zig-new-relationship-llvm].
Additional benefits include less reliance on external dependencies.
Of course,
switching away from a widely used backend library also has disadvantages,
the most prominent being that it requires to re-acquire the knowledge
and implementation quality of a library over a much longer time frame.

[^zig-new-relationship-llvm]: https://kristoff.it/blog/zig-new-relationship-llvm/
