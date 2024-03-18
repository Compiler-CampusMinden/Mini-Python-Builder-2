# SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors
#
# SPDX-License-Identifier: CC0-1.0
#
# Development environment configuration © 2024 by Mini-Python Builder Contributors
# is marked with CC0 1.0

ARG DEBIAN_RELEASE_NAME

FROM debian:$DEBIAN_RELEASE_NAME-slim

ARG LLVM_VERSION=16

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
    # needed for downloading llvm.sh
        wget ca-certificates \
    # dependencies needed by llvm.sh
        lsb-release wget software-properties-common gnupg \
    # needed by c-runtime build
        build-essential \
    # install llvm (https://apt.llvm.org/)
    && wget https://apt.llvm.org/llvm.sh && chmod +x llvm.sh && ./llvm.sh $LLVM_VERSION all && rm llvm.sh \
    # install packages needed for wasm cross compliation
    && apt-get install -y --no-install-recommends \
        libc++abi-16-dev-wasm32 libc++-16-dev-wasm32 libclang-rt-16-dev-wasm32 \
    # cleanup
    && apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/*

# use the appropriate tools/flags for cross compilation
ENV CC_WASM clang-$LLVM_VERSION --target=wasm32-unknown-wasi
ENV LDFLAGS_WASM -fuse-ld=/usr/bin/wasm-ld-$LLVM_VERSION
