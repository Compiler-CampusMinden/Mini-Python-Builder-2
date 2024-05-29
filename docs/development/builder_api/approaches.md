# Approaches to Program-Builder APIs

Compilers and their parts come in various shapes and forms.
Some are tailored to parse and compile a specific language,
while others support multiple frontends (like [GCC](https://gcc.gnu.org/onlinedocs/gcc-14.1.0/gccint/Front-End.html)),
and still others are intended to be fully generic (e.g. MLIR).

While supporting multiple frontend (i.e. multiple languages)
is not relevant for the Builder API,
supporting different backends has become an explicit goal.
Thus the question arises,
in which way the different backends can be represented in the public Builder API.
Mapping the above mentioned flavors onto the current Builder API
and possible future changes to it,
the following chapters aim to present the approaches
and give an overview of the different (dis)advantages
of adopting them.
