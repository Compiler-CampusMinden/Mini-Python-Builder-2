# Backends: wasm

The WASM backend generates WebAssembly code (in the text, not in the binary format).
It then calls a standalone WebAssembly Runtime binary,
to execute the generated code.
In contrast to the `c-runtime`,
this approach only depends on a single binary,
and not a complete compiler toolchain,
thus allowing to generate and execute the code
without installing any further software (by packaging a WebAssembly Runtime as part of the .jar-archive of the builder).

In order to reduce the development effort required,
the generated WebAssembly code also uses
the `c-runtime`.
A crucial difference is that the `c-runtime`
is compiled to WASM(/WASI),
and thus the `c-runtime` library can be supplied as part of the builder's .jar-archive.

In general,
the WASM API works quite similar to the CBuilder API.
WASM poses some unique challenges though,
since its data and memory models
aren't equivalent to the respective models of the C language.

## Using the `c-runtime` from WASM

The `c-runtime` WASM library exports all functions
that are needed to create literals, functions, objects, etc.
These functions are then imported by the generated WASM code,
and the WASM runtime hooks up the library and the generated code together.

In WASM ,
each WASM module (e.g. the c-runtime library or the generated code)
has one linear memory area that it can use.
Static strings are one example of data that is also stored in this area.
References to this area do not use pointers such as in C,
but simply integers.
This creates a some friction between how the c-runtime expects
to operate and how it actually works in WASM.
To bridge this gap, the `c-runtime` exports some additional functions
when cross-compiled to WASM.

The first set of functions deals with all builtins provided by the c-runtime that are accessed via pointers.
The above text means, that to access these builtins,
the c-runtime has to provide accessor functions to these
builtin pointers,
which return the pointer.
This allows the generated code to get references to these builtins
and pass them back to the c-runtime.
(Using the pointer values looks like it works, i.e. using WASM globals,
but these globals have the type integer for the reasons outlined above
and crucially have different values in the generated code and the c-runtime. This causes all pointer comparisons inside the c-runtime to be buggy, when accessing the builtins via WASM globals.)

The second set deals with the fact
that the memory between the `c-runtime` library module
and the generate code module cannot be easily exchanged.
(For WASI to work, the `c-runtime`'s memory must be exported.
Therefor the only possiblity is,
to import this memory in the generated code.
But this approach poses the challenge,
that now the generated code can overwrite arbitrary parts
of the `c-runtime`'s private (from the view of the c-runtime)
memory *and* the generated WASM module for the `c-runtime`
does not provide any metatdata about how it uses its memory.)
For most purposes this is not an issues,
since all mini-python objects are allocated by the c-runtime.
As noted above WASM does not have a builtin string type,
strings are usually represented as a pointer into linear memory
(possibly including metadata such as the string length).
Also, as was just established, the c-runtime and the generated
code do not share the same memory.
To solve this problem,
the c-runtime exports functions to:
1. allocate a string-wrapper structure and space for the string
2. set the contents of the string (byte by byte)
3. remove the wrapper around the raw string
Do note though,
that this wrapper does by itself
does not validate anything but the length of the initially allocated space for the string.
I.e., for the strings to be correctly used by the c-runtime,
they must be 0-byte terminated (as all other strings in plain c),
but this is not enforced, assisted or checked by the c-runtime.

In a similar way,
all functions passed from the generated code to the `c-runtime`
need different handling in WASM,
compared to the pure C code from the CBuilder.
This is because WASM has no function pointer concept.
Instead,
functions are stored in tables.
Because there's currently no way to access multiple (or a specific) table(s)
in C,
the way this works is by exporting the default function table used
in the `c-runtime`,
importing this table in the generated code and appending the generated functions
one by one to this table.

### Example

The following, heavily commented, WebAssembly code
shows how code using the c-runtime could look like.

```wast
(module
  ;; import required function from c-runtime library
  (import "mpy_runtime" "__mpy_builtins_setup" (func $__mpy_builtins_setup))
  (import "mpy_runtime" "__mpy_obj_init_str_static" (func $__mpy_obj_init_str_static (param i32) (result i32)))
  (import "mpy_runtime" "__mpy_call" (func $__mpy_call (param i32 i32 i32) (result i32)))
  (import "mpy_runtime" "__mpy_obj_ref_dec" (func $__mpy_obj_ref_dec (param i32)))
  (import "mpy_runtime" "__mpy_obj_init_tuple" (func $__mpy_obj_init_tuple (param i32) (result i32)))
  (import "mpy_runtime" "__mpy_tuple_assign" (func $__mpy_tuple_assign (param i32 i32 i32) (result i32)))
  (import "mpy_runtime" "__mpy_builtins_get_fn_print" (func $__mpy_builtins_get_fn_print (result i32)))
  (import "mpy_runtime" "__mpy_str_alloc" (func $__mpy_str_alloc (param i32) (result i32)))
  (import "mpy_runtime" "__mpy_str_set" (func $__mpy_str_set (param i32 i32 i32)))
  (import "mpy_runtime" "__mpy_str_into_cstr" (func $__mpy_str_into_cstr (param i32) (result i32)))
  ;; this function represents the execution of the global scope in python
  ;; (i.e. the main function generated by the cbuilder).
  ;; it is accessed by this name by the code setting up
  ;; the WASM execution
  (func (export "mpy__main__")
        ;; this code uses two local variables:
        ;; 1. pointer to the allocated string
        ;; 2. iterator variable for the loop copying the string from
        ;;    this module's memory into the c-runtime's memory
        (local i32 i32)
        call $__mpy_builtins_setup ;; mandatory setup

        ;; this code represents the python code `print("Hello World")`
        ;; translated into c-runtime function calls,
        ;; this equals
        ;; __mpy_call(
        ;;   MPy_Func_print,
        ;;   __mpy_tuple_assign(
        ;;     0
        ;;     __mpy_obj_init_str_static("Hello World"),
        ;;     __mpy_obj_init_tuple(1)
        ;;   )
        ;; )

        ;; the global has a different value here, so need to define & use getters
        ;; for all global pointers
        call $__mpy_builtins_get_fn_print ;; first arg (__mpy_call)

        ;; second arg (__mpy_call): tuple with one arg, the string to print

        ;; first arg (__mpy_tuple_assign)
        i32.const 0

        ;; second arg (__mpy_tuple_assign)

        ;; first: copy the string from this modules memory into cruntime
        ;; this is invisible in the cruntime call chain above,
        ;; but roughly amounts to:
        ;; 1. initialise new string wrapper (__mpy_str_alloc(len))
        ;; 2. copy string char by char (__mpy_str_set(str, pos, val))
        ;; 3. unwrap the string wrapper into the raw char* pointer (which equals an i32 in wasm)
        ;;    (__mpy_str_into_cstr)

        i32.const 12 ;; length of the string "Hello World" + null-byte
        call $__mpy_str_alloc ;; allocate a new string wrapper
        local.set 0 ;; store a pointer to this string,
                    ;; we will need this string multiple times

        i32.const 4 ;; the string starts at offset 4 in our local memory
        local.set 1 ;; store this start value in the iterator variable
        loop
            ;; get the string wrapper (first arg __mpy_str_set)
            local.get 0
            ;; get the position at which to set (second arg __mpy_str_set)
            local.get 1
            i32.const 4
            i32.sub ;; our iterator is offset to the string's position in memory
                    ;; remove this offset for the position in the raw string
            ;; load char at pos i (third arg __mpy_str_set)
            local.get 1
            i32.load

            call $__mpy_str_set

            ;; i++
            local.get 1
            i32.const 1
            i32.add
            local.tee 1

            ;; continue if i <= 11 (string length + null-byte)
            i32.const 11
            i32.const 4 ;; offset
            i32.add
            i32.le_s
            br_if 0
        end

        ;; get reference to string wrapper (first arg __mpy_str_into_cstr)
        local.get 0

        call $__mpy_str_into_cstr
        ;; now the stack contains a reference to the raw string (first arg __mpy_obj_init_str_static)

        call $__mpy_obj_init_str_static
        ;; now the stack contains a reference to a minipython string object
        ;; (seoncd arg __mpy_tuple_assign)

        i32.const 1 ;; first arg __mpy_obj_init_tuple
        call $__mpy_obj_init_tuple
        ;; now the stack contains a reference to a minipython tuple
        ;; (third arg __mpy_tuple_assign)

        call $__mpy_tuple_assign
        ;; now the stack contains a reference to a minipython tuple,
        ;; but with an assigned element
        ;; (second arg __mpy_call)

        i32.const 0 ;; third arg __mpy_call

        call $__mpy_call

        ;; now the stack only contains the return value of the function call.
        ;; Because wasm requires the stack be empty at the end of a method,
        ;; do proper reference counting here...
        call $__mpy_obj_ref_dec
        )
  ;; create the linear memory for this module
  (memory (export "memory") 1)
  ;; write the string "Hello World\00" into this modules
  ;; memory at offset 4
  (data (i32.const 4) "Hello World\00"))
```
