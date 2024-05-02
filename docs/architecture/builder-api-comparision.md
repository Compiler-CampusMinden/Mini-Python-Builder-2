# Comparison of Builder APIs

- both backends + generic API 'do the same thing but different'
- this has multiple drawbacks:
    - confusing for users
    - more difficult to maintain:
        - e.g. discussing is more difficult, because most things don't follow
            a clear naming scheme and there are no canonical names
        - difficult to navigate: there's no clear location if I search, e.g., for the implementation of function declarations
    - the same problems need to be solved n-times, due to the APIs only being *almost, but not quite* the same
- solutions:
    - develop (or adapt? [PEP7 - Style Guide for C Code](https://peps.python.org/pep-0007/) could be an inspiration) a naming scheme / nomenclature and write it down somewhere, very visible (i.e ../../contrib)
    - analyze current APIs:
        - where do they differ?
        - why do they differ? (i.e. what are the underlying reasons for these differences? different architectural choices during development? constraints given by technologies used (e.g. does wasm in comparison to c force doing things differently?)
        - how can they be reconciled? / which way is the best (least effort, maintainability going forward, least differences between APIs, openeness to the next backend with different constraints, 'richness', e.g. is referencing tracked and validated by the the API)

## API Analysis - Differences

- table or something unsuitable, due to amount of data available
- diagram is neat for overview, but fails at visualising different aspects
- therefore: aspect focused analysis
- first steps: which aspects to take into account?
    - naming -> no, already covered
    - overall structure
    - api paradigm (e.g. builder pattern, etc)
    - referencing (of variables, functions, builtins, etc)
    - ownership tracking/backreferences in the 'graph' formed by the API objects
    - output -> not focus on this, but should be handled (i.e. no (intentionally maintained) way vs manual formatting in cbuilder vs Line/Block construct WASM)

### Overall Structure

- broadly the same (differs mostly in naming)
    - top-level (global scope) containing:
        - statements to execute
        - function declarations/definitions
        - class declarations/definitions
        - global variable declarations/definitions
    - below that 'graph' formed by references between API objects (e.g. functions, classes, calls (function, type i.e. object creation), etc)
- some functionality in different locations (does this fall under naming too?)
    - functions
    - boolean and/or/not, while/if
    - call
- some functionality does not exists across all APIs:
    - super call (not in WASM) - see referencing
    - builtins (not in CBuilder) - see referencing
    - reference (only in CBuilder) - see referencing
- some functionality is (internally) structured differently:
    - if/else
    - cbuilder always uses Lists, while generic/wasm uses Sets where no explicit order is required
    - String vs StringLiteral

### API Paradigm

- depends on exact concept (i.e function declaration, class declaration, etc), even inside the same API
- in general two approaches:
    - addXXX(XXX) methods (used by the CBuilder.ProgramBuilder and in various WASM classes)
        - this is used by WASM, to limit creation of certain classes to builder internal addXXX methods (e.g. variable declarations), allowing automatic scope inference (and by extension: ownership tracking & verification) - see below
    - immutable data containers that receive all data as part of the constructor (WASM/Generic use Records to cut down on boilerplate where possible)
        - though it is not enforced that the data received (e.g. `Set`s, `List`s) itself is immutable too - modifying data after adding it to a builder API container shouldn't cause any problems (although this isn't confirmed for all cases, this would be very suprising)

### Referencing

- cbuilder:
    - everything is referenced by name, i.e. to reference something create a new Reference instance with the name of $thing
    - no special handling of builtins - those are simply referenced by their name via Reference
    - exception super()/SuperCall
- generic:
    - nothing can be referenced by name, i.e. there is no Reference class
    - user created things (functions, classes, variables) can be referenced via the instance that defines $thing (i.e. to reference a global variable a, the VariableDeclaration instance for a is used)
    - builtins are referenced with a class that exists soley for the purpose of referencing that builtin
    - exception super()/SuperCall
- wasm:
    - nothing can be referenced by name, i.e. there is no Reference class
    - user created things (functions, classes, variables) can be referenced via the instance that defines $thing (i.e. to reference a global variable a, the VariableDeclaration instance for a is used)
    - builtins are referenced with a class that exists soley for the purpose of referencing that builtin
    - *no* exception for super()/SuperCall - user must take care to pass self (via reference to the self VariableDeclaration created as an argument of the `__init__` function)

### Ownership Tracking / Back-/Parent-References

- cbuilder: none
- generic: implicit (e.g. Assignment has a reference to the VariableDeclaration the assignment assigns to)
- wasm: explicit (e.g. VariableDeclaration has an `owner`, i.e. the function/class the declaration is scoped to)

### Output

- cbuilder:
    - formatting: manually formatted (i.e. indent is estimated on a 'best effort' basis, there's no structured handling to it)
    - form/location: whole c-runtime source code + generated program.c file in a user chosen directory
- generic: has no output
- wasm:
    - formatting Line/Block classes + BlockContent interface to abstract over nesting of generated code
    - form/location: temporary file, only kept if wasmtime (executor) exits with error

## API Analysis - Causes

### Structure: Differing Locations

- inconsistent naming
- missing structure - i.e. there's no documentation on how the existing structure works or why it was chosen
- this caused: ad-hoc decisions during implementation (i.e. while implementing the cbuilder location `a` made sense, then during generic implementation `b` seemed more appropriate, and finally, implementing wasm `c` seemed the best option - but the others were never adjusted)

### Structure: Differing Internal Structure

- if/else: caused by technical necessities:
    - for the cbuilder: if/else if/else are independent of each other (in the sense that each of them is a self-contained block of code) - the internal structure represents and uses this to deduplicate code
    - for wasm: the elif/else are nested inside (themselves)/the if - they aren't self-contained but intertwined - the internal structure represents this to ease implementation
- string vs stringliteral: same
    - c has string literals
    - wasm does not (at least not in the way C has them)
- set vs list: see the above point above ad-hoc decisions
    - this was discussed in https://github.com/Compiler-CampusMinden/Mini-Python/issues/104
    - the generic/wasm APIs follow the 'using is a list for encoding positions is more natural' argument - while also explicitly using Sets when there's no natural position
    - the difference between implicit/explicit index is currently handling in the Generic->CBuilder transformation

### API Paradigm

- different requirements/goals
    - e.g. WASM's scope inference/ownership tracking
    - simplification: having the final object by simply calling the constructor is much easier
    - for collections (lists, sets, maps), especially in the function/ program/module / class context, if no addXXX method is provided, such a method often needs to be created ad-hoc by retaining a reference to the original collection past the constructor invocation

- i.e., the ProgamBuilder simply has the addXXX methods because it felt more natural to have them
- while the generic APIs equivlante MPyModule does *not* have them,
    simply because it was less code to write to omit them...

### Referencing / Structure: Non-Uniform Functionality

- root-cause: feature request for generic/wasm API to require the API user to maintain references to declaration/definition, e.g. symbol tables - i.e. referencing by only name contradicts this goal
- for cbuilder, referencing stuff just by name simply was convenient
    - e.g., builtins did not need any consideration: since the name is known, all builtins are simply available via `Reference`
- for wasm, this is technically impossible:
    - most (all?) builtins are pointers allocated at runtime
    - due to how wasm works, those need to be acquired via function calls by the generated wasm code
    - therefore the builtins need dedicated treatment in the WASM API
    - dedicated builtin classes both solve the technical need and the feature request
- for wasm, the name alone is not enough:
        - wasm differentiates between local/global variables
        - therefore, each reference to a variable would need to 'know' whether its global or local
        - keeping the Reference class idea would make for a much less ergonomic & more error prone API (e.g. `new Reference("a", global=true)`)
        - using the original declaration/definition as the reference satisifies both the feature request, the technical need and a more pleasant API
- SuperCall:
    - in Cbuilder: ensure the minipython `super()` call is translated correctly - in the c-runtime it really is `super(self)`
    - in generic/wasm: the API is 'grown up' now anyway (regarding requirement to maintain symbol table by API user) - modelling super as a built-in and requiring the user to inject self isn't such a huge step backwards
- for generic:
    - needs to be transformable to both CBuilder/WASM
    - allowing Reference would require the generic API to maintain its own symbol tables for the WASM transformation
    - for super call: leaving it out requires identifying and removing the self argument from the super call, while translation to wasm is straightforward (prepend self to argument list)

### Ownership Tracking / Back-/Parent-References

- cbuilder:
    - cbuilder generates c-code: scopes are handled by the compiler translation the c-code to machine code
    - all references are handled as strings
    - there's absolutely no need to know the parent:
        - not on a technical level required by the target language
        - not by a feature of the API (such as using the original defintion/declaration for referencing $thing)
- generic:
    - no conscious decision
    - because the original declaration/definition object is used as a reference, this occurrs naturally
- wasm:
    - for wasm, variables have a global/local scope
    - functions/classes are MiniPython objects in the end, and therefore variables too, functions/classes/variables are all afected by this
    - in order to generate correct code, declarations need to know if they are global/local
    - if declarations receive their parent on creation, they 'know' if they're global/local automatically - creating a much nicer API
    - validating that declarations are only referenced in the correct scopes is possible now

### Output

- c-code is fairly readable anyway, as long as there's a linebreak between each statement
- for wasm, that is not so much the case (due to operating on a much lower level) - therefore correct indentation was more valuable during implementation of the wasm API
    - the Block/line abstraction neatly hides the actual calculation of indent, while basically allowing the same freedom as free-form strings
- c-builder was simply not touched so far

## API Analysis - Adjustments

### Structure: Differing Locations

- when a naming scheme exists, adjust package/class/method names
- create and apply a common structure based on the naming scheme

### Structure: Differing Internal Structure

- if/else: this is fine, as long as the public API is uniform (enough)
- string/stringliteral: unsure, but probably okay (since technical necessary for wasm, but unnecessary complication for non-wasm)
    - does not complicate transformation - so there's no technical reason for pursuing a uniform interface here
- set vs list: nice to have, but trivial to transform for now - leave to already open ticket (but document there that wasm/generic already follow a different approach)

### API Paradigm

- both approaches are valid on their own
- both approaches probably have their use
    - having addXXX methods for VariableAssignment would simply be overkill
    - not having them in, e.g. the top-level (ProgramBuilder/MPyModule) is inconvenient
- approach used for each specific concept should be consistent across the CBuilder/WASM/Generic APIs though

### Referencing / Structure: Non-Uniform Functionality

- this split will exist as long as the CBuilder API is kept backwards compatible, i.e. retains its 'referencing by name' mechanism
- for wasm/generic, adding Reference is either technically difficult, or contradicts explicit features of the API

- super call: our super() is a `super().__init__` in reality! or, in python `3.<some version` terms, a `super(self, ParentClass).__init__` - that is, our super is an oversimplification and therefore plainly wrong.
        > Also note that, aside from the zero argument form, super() is not limited to use inside methods. The two argument form specifies the arguments exactly and makes the appropriate references. The zero argument form only works inside a class definition, as the compiler fills in the necessary details to correctly retrieve the class being defined, as well as accessing the current instance for ordinary methods.
        (cf. https://docs.python.org/3/library/functions.html#super)
    - when this gets corrected:
        - super should exist only as the one/two argument form (i.e. *not* the syntactic sugar form without arguments)
        - then it can be treated the same way other builtins are and doesn't need any special handling in any of the 3 apis anymore

### Ownership Tracking / Back-/Parent-References

- generic and wasm need to have a common model, for transformation to work properly - in other words, the current status (generic implicitvs wasm explicit) doesn't allow transformation, because crucial information is missing during transformation and can't be easily reconstructed (the explicit owner info)
- adding this info the generic API aligns with the conscious decisions that shaped the wasm api, and is therefore the preferred option

### Output

In case the CBuilder output needs to have better readability,
the Line/Block model of WASM can be adopted.
Until then, this is an internal implementation detail,
with no big relevance.
