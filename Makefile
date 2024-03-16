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

# render (plantuml) diagrams in the docs/ folders
.PHONY: docs-diagrams
docs-diagrams:
	$(PLANTUML) -tsvg ./docs/**/*.puml

# render the docs/ with mdbook
.PHONY: book
book: docs-diagrams
	mdbook build ./docs/

.PHONY: book-docker
book-docker:
	@docker image inspect mpy-mdbook:latest 1>/dev/null 2>&1 || (echo "docker image not found; please run 'make docker-build-mdbook'" && exit 1)
	docker run \
		-v "$(CURDIR):/workdir" \
		-it --rm mpy-mdbook:latest \
		sh -c \
		'cd /workdir && make book'


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

.PHONY: docker-build-mdbook
docker-build-mdbook:
	docker build --tag mpy-mdbook:latest docker/book
