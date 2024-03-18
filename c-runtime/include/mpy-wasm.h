// SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors
//
// SPDX-License-Identifier: MIT
//
// This work by Mini-Python Builder Contributors is licensed under MIT.
#ifndef MPY_WASM_H
#define MPY_WASM_H

#ifndef MPY_WASM
// uncomment the following line for development
// of this file or mpy-wasm.c
// in case your editor shows the rest of this file(s) as inactive.
// Though if you are using the clangd LSP, you should prefer to use
// the make recipe `compilation-database`.
// #define MPY_WASM
#endif

// this is only needed on WASM,
// due to how memory works there
#ifdef MPY_WASM

#include "mpy.h"
#include "mpy_obj.h"

typedef struct __MPyAllocedStr {
    unsigned int len;
    char *str;
} __MPyAllocedStr;

/**
 * Allocate & return memory for a new c string of length \a len.
 *
 * @param len Length of the string, including its final null-byte.
 * @return Pointer to the allocated memory.
 */
MPY_API __MPyAllocedStr* __mpy_str_alloc(unsigned int len);

MPY_API void __mpy_str_set(__MPyAllocedStr *str, unsigned int index, char value);

MPY_API char* __mpy_str_into_cstr(__MPyAllocedStr *str);

// make builtins accessible to other wasm modules
// (the pointers are exported as globals, but these
// globals have different values than the pointer itself,
// breaking all C code that compares __MPyObj* pointers
// against builtin __MPyObj* symbols)
MPY_API __MPyObj *__mpy_builtins_get_fn_print(void);

#endif /* MPY_WASM */

#endif
