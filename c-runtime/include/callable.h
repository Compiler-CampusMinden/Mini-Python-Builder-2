#ifndef CALLABLE_H
#define CALLABLE_H

#include "mpy.h"
#include "mpy_obj.h"

typedef __MPyObj* (*__mpy_func_call) (__MPyObj *args, __MPyObj *kwargs);

MPY_API __MPyObj* __mpy_call(__MPyObj *function, __MPyObj *args, __MPyObj *kwargs);

#endif
