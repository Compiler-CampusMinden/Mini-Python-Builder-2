# Comparison

The specific API provides a very straightforward implementation path
and a simple implementation.
Since it achieves this through a very restricted feature set,
it does not work for the use case of the Mini-Python builder,
namely supporting multiple backends.
While the MLIR approach of developing a generic tree-like structure,
on which transformations can be express declaratively and generically provides useful abstractions,
it brings a lot of complexity.
Therefore, a solution that that lies in the middle,
seems like the appropriate choice.
