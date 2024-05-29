# MLIR

MLIR is basically the opposite of a backend specific API:
It's stated goal is to be completely generic,
that is supporting different control flow models,
different type systems, etc.

Underlying MLIR is a generic IR representation.
For the purpose of differentiating MLIR from the other approaches,
one key element of MLIR's IR is that it forms
a specialised, but generic tree.
That is, the general tree has a specified form,
but the elements stored in the tree can be refined
and extended for the required usages of the implemented language.
