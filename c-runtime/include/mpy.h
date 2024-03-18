// SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors
//
// SPDX-License-Identifier: MIT
//
// This work by Mini-Python Builder Contributors is licensed under MIT.

#ifndef MPY_H
#define MPY_H

#if defined _WIN32 || defined __CYGWIN__
  #define MPY_HELPER_DLL_IMPORT __declspec(dllimport)
  #define MPY_HELPER_DLL_EXPORT __declspec(dllexport)
  #define MPY_HELPER_DLL_LOCAL
#else
  #if __GNUC__ >= 4
    #define MPY_HELPER_DLL_IMPORT __attribute__ ((visibility ("default")))
    #define MPY_HELPER_DLL_EXPORT __attribute__ ((visibility ("default")))
    #define MPY_HELPER_DLL_LOCAL __attribute__ ((visibility ("hidden")))
  #else
    #error unsupported compiler version
  #endif
#endif

// MPY_API is used for the public API symbols. It either DLL imports or DLL exports (or does nothing for static build)
// MPY_LOCAL is used for non-api symbols.

#ifdef MPY_DLL // defined if dynamically linked
  #ifdef MPY_DLL_EXPORTS // defined if building shared library (instead of using)
    #define MPY_API MPY_HELPER_DLL_EXPORT
  #else
    #define MPY_API MPY_HELPER_DLL_IMPORT
  #endif // MPY_DLL_EXPORTS
  #define MPY_LOCAL MPY_HELPER_DLL_LOCAL
#else // MPY_DLL is not defined: building static lib
  #define MPY_API
  #define MPY_LOCAL
#endif // MPY_DLL

#endif
