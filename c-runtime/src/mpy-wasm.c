// SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors
//
// SPDX-License-Identifier: MIT
//
// This work by Mini-Python Builder Contributors is licensed under MIT.
#include "mpy-wasm.h"

#include <stdlib.h>
#include <string.h>

#include "builtins-setup.h"
#include "checks.h"
#include "errors.h"

#ifdef MPY_WASM

__MPyAllocedStr* __mpy_str_alloc(unsigned int len) {
    __MPyAllocedStr *str = __mpy_checked_malloc(sizeof(__MPyAllocedStr));
    str->len = len;
    str->str = __mpy_checked_malloc(len);

    return str;
}

void __mpy_str_set(__MPyAllocedStr *str, unsigned int index, char value) {
    if (index >= str->len) {
        __mpy_fatal_error(__MPY_ERROR_USER);
    }
    str->str[index] = value;
}

char* __mpy_str_into_cstr(__MPyAllocedStr *str) {
    return str->str;
}


MPY_API __MPyObj *__mpy_builtins_get_fn_print(void) {
    return __MPyFunc_print;
}

__MPyObj *__mpy_builtins_get_fn_super(void) {
    return __mpy_super;
}

__MPyObj *__mpy_builtins_get_type_object(void) {
    return __MPyType_Object;
}

__MPyGetArgsState* __mpy_args_init_malloced(const char *funcName, __MPyObj *args, __MPyObj *kwargs, unsigned int countPositionalArgs) {
    __MPyGetArgsState *state = __mpy_checked_malloc(sizeof(__MPyGetArgsState));

    __MPyGetArgsState stateOnStack = __mpy_args_init(funcName, args, kwargs, countPositionalArgs);

    memcpy(state, &stateOnStack, sizeof(__MPyGetArgsState));

    return state;
}

void __mpy_args_finish_malloced(__MPyGetArgsState *state) {
    __mpy_args_finish(state);
    free(state);
}

#endif /* MPY_WASM */
