# Backend Agnostic Java API

This is a middle ground between the fully generic
and the fully specific approaches of MLIR and the backend specific Java API, respectively.

A very simple approach to achieve a backend generic API,
is to have two layers:
1. a public top layer, exposing a generic API
2. a private layer, containing implementations of the backend specific APIs

While MLIR could certainly be used to faciliate such a system,
a far simpler approach is,
to simply provide a means of converting the data structures of the top layer into each of the data structures of the private layer.
