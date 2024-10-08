(module
  ;; import required function from c-runtime library
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
