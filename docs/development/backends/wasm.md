<!--
SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors

SPDX-License-Identifier: CC-BY-SA-4.0

Mini-Python Builder documentation © 2024 by Mini-Python Builder Contributors is licensed under CC BY-SA 4.0.

-->

# WebAssembly

[WebAssembly](https://webassembly.org/)
(often shortened to WASM or Wasm)
is a "binary instruction format for a stack-based virtual machine"[^wasm-hp-citation].
The WebAssembly 
[specification(s)](https://webassembly.org/specs/)
are maintained by the [W3C](https://www.w3.org/community/webassembly/).
Originally designed to allow replacement of JavaScript on the web in cases where higher performance beyond what JavaScript allows is needed,
today it is used as a generic, embeddable compilation target,
supported by various programming languages.
The WASM homepage currently lists 
[14 supported languages](https://webassembly.org/getting-started/developers-guide/)including C/C++, Go, Kotlin, Rust).

Due to its sandboxed nature WASM is often favored in security sensitive context, browsers being a prime example, other possible use-cases include plugin-based architectures.
Another important aspect of WASM is its platform independence,
and the fact that WASM code cannot only be represented in binary form,
but also has a human readable (
and also writable)
text representation based on 
[S-expressions](https://en.wikipedia.org/wiki/S-expression).

While WASM itself is architecture/platform independent,
a WebAssembly runtime implementing a WASM VM usually
provides a WebAssembly API to the code running,
to allow interoperation between the host 
(i.e. where the VM is running, e.g. a browser)
and the WebAssembly code.
This is needed, because WebAssembly itself only operates on numbers.
The most prominent APIs in use today are either JavaScript/
[Browser](https://developer.mozilla.org/en-US/docs/WebAssembly/Loading_and_running)
based,
or an API called [WASI, the WebAssembly System Interface](https://wasi.dev).
WASI provides
"several operating-system-like features, including files and filesystems"[^wasi-intro]
in a standardized manner.

[^wasm-hp-citation]: <https://webassembly.org>
[^wasi-intro]: <https://github.com/bytecodealliance/wasmtime/blob/a0505b18b816cee61ed86f606563b9d5e40e698b/docs/WASI-intro.md>
