# SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors
#
# SPDX-License-Identifier: CC0-1.0
#
# Development environment configuration © 2024 by Mini-Python Builder Contributors
# is marked with CC0 1.0

PLANTUML ?= plantuml

define LIST_NOT_SUPPORTED
note: your make is too old (<= 4.4.1) \
and doesn't natively support listing targets
endef
.PHONY:
list:
	@($(MAKE) --list-targets >/dev/null 2>&1 && $(MAKE) --list-targets) \
		|| echo "$(LIST_NOT_SUPPORTED)"

# remove various build folders & artifacts
.PHONY: clean
clean:
	$(RM) -r ./docs-rendered
	$(MAKE) --directory c-runtime clean

PUML_SOURCES := $(shell find ./docs -type f -iname '*.puml')
PUML_TARGETS := $(patsubst %.puml,%.svg, $(PUML_SOURCES))

# render (plantuml) diagrams in the docs/ folders
$(PUML_TARGETS) : docs/%.svg: docs/%.puml
	$(PLANTUML) -tsvg $<

# render the docs/ with mdbook
.PHONY: book
book: $(PUML_TARGETS)
	mdbook build ./docs/

.PHONY: book-docker
book-docker:
	@docker image inspect mpy-mdbook:latest 1>/dev/null 2>&1 || (echo "docker image not found; please run 'make docker-build-mdbook'" && exit 1)
	docker run \
		-v "$(CURDIR):/workdir" \
		-it --rm mpy-mdbook:latest \
		sh -c \
		'cd /workdir && make book'

.PHONY: c-runtime/lib_wasm
c-runtime/lib_wasm:
	$(MAKE) --directory c-runtime lib_wasm

.PHONY: c-runtime/lib_wasm-docker
c-runtime/lib_wasm-docker:
	@docker image inspect mpy-cruntime-wasm:latest 1>/dev/null 2>&1 || (echo "docker image not found; please run 'make docker-build-c-runtime.wasm'" && exit 1)
	docker run \
		-v "$(CURDIR):/workdir" \
		-it --rm mpy-cruntime-wasm:latest \
		sh -c \
		'cd /workdir && make c-runtime/lib_wasm'

EXAMPLES := $(addprefix example/,$(notdir $(wildcard builder-examples/main-classes/*)))
.PHONY: $(EXAMPLES)
$(EXAMPLES):
# gradle sometimes swallows stdout content
# when executing via `./gradlew run` - prevent that
# by running the examples outside of gradle
	./gradlew installDist
# note: gradle sets the working directory to the project directory
# before execution - do this here too
	cd builder-examples && ./build/install/builder-examples/bin/$(@F)

.PHONY: test
test:
	./gradlew test

# check formatting rules are adhered to
.PHONY: format
format:
	nix fmt -- --check flake.nix nix/ || echo "nix not installed, skipping *.nix files"
	eclint -exclude "{flake.lock,LICENSES/**}"

# (try to) automatically fix formatting issues
.PHONY: format-fix
format-fix:
	nix fmt -- flake.nix nix/ || echo "nix not installed, skipping *.nix files"
	eclint -exclude "{flake.lock,LICENSES/**}" --fix

# check REUSE compliance (i.e. ensure that all files have copyright information)
.PHONY: reuse
reuse:
	reuse lint

# update nix dependencies via docker; i.e. without having nix installed
.PHONY: flake-update-docker
flake-update-docker:
	docker run \
		-v "$(CURDIR):/workdir" \
		-it --rm nixos/nix \
		sh -c \
		'cd /workdir && nix --extra-experimental-features nix-command --extra-experimental-features flakes flake update'

COMMON_DOCKER_BUILD_ARGS :=
# if all docker images use a common base image,
# at least some part of the development images
# share common layers.
# this should save storage (and initial download time)
COMMON_DOCKER_BUILD_ARGS += --build-arg DEBIAN_RELEASE_NAME=bookworm

.PHONY: docker-build-mdbook
docker-build-mdbook:
	docker build $(COMMON_DOCKER_BUILD_ARGS) --tag mpy-mdbook:latest docker/book

.PHONY: docker-build-c-runtime.wasm
docker-build-c-runtime.wasm:
	docker build $(COMMON_DOCKER_BUILD_ARGS) --tag mpy-cruntime-wasm:latest docker/c-runtime.wasm
