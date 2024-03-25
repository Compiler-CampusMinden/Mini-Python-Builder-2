# SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors
#
# SPDX-License-Identifier: CC0-1.0
#
# Development environment configuration © 2024 by Mini-Python Builder Contributors
# is marked with CC0 1.0
{
  inputs.flake-compat = {
    url = "github:edolstra/flake-compat";
    flake = false;
  };

  # update this url to github:nixos/nixpkgs/nixos-unstable
  inputs.nixpkgs.url = "github:liketechnik/nixpkgs/init/mdbook-pandoc";

  outputs = {
    self,
    nixpkgs,
    ...
  } @ inputs: let
    forSystems = function:
      nixpkgs.lib.genAttrs [
        "x86_64-linux"
      ] (system: let
        pkgs = import nixpkgs {
          inherit system;
        };
      in
        function {inherit system pkgs;});
  in {
    formatter = forSystems ({pkgs, ...}: pkgs.alejandra);

    devShells = forSystems ({
      pkgs,
      system,
      ...
    }: let
      defaultShell = {
        packages = with pkgs; [
          bashInteractive # the default bash supplied by mkShell
                          # does not support interactive use
          gnumake

          reuse
          eclint

          mdbook
          plantuml

          # compilation of the c-runtime to wasm (linker, etc)
          pkgs.pkgsCross.wasi32.stdenv.cc.bintools
          # web-assembly binary toolkit; i.e. inspecting wasm files & co
          wabt
          # webassembly runtime
          wasmtime

          # compilation database for c language server for c-runtime dev
          bear

          # builder/c-runtime tests
          gcc
          clang
          clang-tools
          valgrind

          # c-runtime docs
          doxygen
        ];

        JAVA_HOME = pkgs.openjdk_headless.home;

        # compilation of the c-runtime to wasm (compiler)
        CC_WASM="${pkgs.pkgsCross.wasi32.stdenv.cc}/bin/${pkgs.pkgsCross.wasi32.stdenv.cc.targetPrefix}cc";

        MPY_BUILDER_WASM_USE_SYSTEM_WASMTIME = 1;

        shellHook = ''
          unset SOURCE_DATE_EPOCH
          echo "make <task>:"
          make list
        '';
      };
    in {
      default = pkgs.mkShell defaultShell;
      with-texlive = pkgs.mkShell (defaultShell
        // {
          packages =
            defaultShell.packages
            ++ (with pkgs; [
              librsvg
              mdbook-pandoc
              texlive.combined.scheme-full
            ]);

          FONTCONFIG_FILE = pkgs.makeFontsConf {
            fontDirectories = with pkgs; [
              # serif, math
              stix-two
              # sans-serif/mono
              recursive
              # emoji
              noto-fonts-emoji
            ];
          };
        });
    });
  };
}
