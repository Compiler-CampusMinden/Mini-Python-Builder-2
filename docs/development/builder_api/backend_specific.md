# Backend Specific

This is essentially the original CBuilder API.
It's a special purpose API that targets exactly a single target format.

The advantage is, that it is very straightforward to implement,
since there are no differences in the target formats that might need an abstraction.
Essentially, all parts of the program can be hardcoded.

This also has the implication,
that there is no explicit graph or tree structure required,
to implement such a system.
Instead, references that form an implicit tree/graph can be used,
to traverse the program representation and generate the respective code.
