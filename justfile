# SPDX-FileCopyrightText: 2024 Mini-Python Builder Contributors
#
# SPDX-License-Identifier: CC0-1.0
#
# Development environment configuration © 2024 by Mini-Python Builder Contributors
# is marked with CC0 1.0

# override to configure which plantuml executable to use,
# e.g. to 'just plantuml="java -jar /path/to/plantuml.jar" docs-diagrams'
plantuml := "plantuml"

default:
    @{{just_executable()}} --list --list-heading $'just <task>:\n'

# remove various build folders & artifacts
clean:
    rm -rf ./docs-rendered

# render (plantuml) diagrams in the docs/ folders
docs-diagrams:
    {{plantuml}} -tsvg ./docs/**/*.puml

# render the docs/ with mdbook
book: docs-diagrams
    mdbook build ./docs/

# check formatting rules are adhered to
format:
    nix fmt -- --check flake.nix nix/
    eclint -exclude "{flake.lock,LICENSES/**}"

# (try to) automatically fix formatting issues
format-fix:
    nix fmt -- flake.nix nix/
    eclint -exclude "{flake.lock,LICENSES/**}" --fix

# check REUSE compliance (i.e. ensure that all files have copyright information)
reuse:
    reuse lint
