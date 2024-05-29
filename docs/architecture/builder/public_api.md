# Public Builder API

The public Builder API is based on the idea
to represent all (Mini-)Python concepts
as individual classes.
These classes then contain references
to other classes,
forming an (implicit) graph.
Each class representing such a concept,
e.g. variable declaration,
implements a `build()` method.
This method returns a string,
representing the concept in the code generation's target language
(e.g. C, WebAssembly).
Of course, such a `build()` implementation
calls the `build()` method of other classes.
Thus the top-level `build()` method effectively traverses the whole
object graph,
yielding a string containg the code
that represents the program.
