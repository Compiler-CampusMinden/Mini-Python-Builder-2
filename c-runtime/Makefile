############
# Programs #
############

MKDIR := mkdir
MKDIR_P := mkdir -p

#############
# Locations #
#############

# make SRCDIR and INCLUDEDIR available to doxygen
export SRCDIR := $(realpath ./src)
export INCLUDEDIR := $(realpath ./include)
DOXYFILE := $(realpath ./Doxyfile)

# make DOCDIR available to doxygen
export DOCDIR := $(abspath ./doc)
BINDIR := $(abspath ./bin)
LIBDIR := $(abspath ./lib)
OUTDIR := $(abspath ./out)
OUTDIR_WASM := $(abspath ./out_wasm)

#################
# Default Flags #
#################

# use generic configuration if
# nothing WASM specific has been set
CC_WASM ?= $(CC)
CFLAGS_WASM ?= $(CFLAGS)
LDFLAGS_WASM ?= $(LDFLAGS)
LOADLIBS_WASM ?= $(LOADLIBS)
LDLIBS_WASM ?= $(LDLIBS)

# defaults independent of compilation target
MPY_CFLAGS += -pedantic -Wall -Werror
MPY_CFLAGS += -std=c11 -I$(INCLUDEDIR)
MPY_CFLAGS += -ffile-prefix-map=$(SRCDIR)=.
# make getline() (see man getline(3)) available
MPY_CFLAGS += -D_POSIX_C_SOURCE=200809L
# only export symbols explicited designated for export
MPY_CFLAGS += -fvisibility=hidden
# export symbols marked with visibility default
# (see mpy.h)
MPY_LDFLAGS += -Wl,--export-dynamic

ifdef DEBUG
MPY_CFLAGS += -ggdb -DMINI_PYTHON_DEBUG=1 -Og
MPY_LDFLAGS += -ggdb -Og
else
# NOTE(FW): possibly disable assertions in the future?
endif

# apply defaults to all targets
CFLAGS += $(MPY_CFLAGS)
CFLAGS_WASM += $(MPY_CFLAGS)
LDFLAGS += $(MPY_LDFLAGS)
LDFLAGS_WASM += $(MPY_LDFLAGS)

# WASI specific additions:

# building WASM with optimisations
# can produce invalid code
# that are difficult to notice
# (e.g. misalignments)
# (cf. https://github.com/bytecodealliance/wasmtime/issues/6768#issuecomment-1651954054)
CFLAGS_WASM += -Og
# make api symbols visible; see include/mpy.h
CFLAGS_WASM += -DMPY_DLL -DMPY_DLL_EXPORTS
# gate wasm only functionality
CFLAGS_WASM += -DMPY_WASM

# 'linking' WASM code docs:
# https://clang.llvm.org/docs/AttributeReference.html#export-name
# https://lld.llvm.org/WebAssembly.html#exports

# WASI distinguishes between reactors and commands,
# reactors being closer to a library,
# i.e. the goal here
# (cf. https://github.com/WebAssembly/WASI/blob/main/legacy/application-abi.md#current-unstable-abi)
LDFLAGS_WASM += -mexec-model=reactor
# ensure that the WASM module really has no entry function and is treated as a library
LDFLAGS_WASM += -Wl,--no-entry
# wasm currently has no function pointers
# (in the sense that functions can be referenced
# as a value just as i32/u32 etc, nor as memory pointers (e.g. strings))
# export the function table used by clang
# so that minipython modules can import it
# and add their functions
# (table is named __indirect_function_table)
LDFLAGS_WASM += -Wl,--export-table -Wl,--growable-table

###########
# Targets #
###########

BIN := $(BINDIR)/program
LIB_WASM := $(LIBDIR)/mpy_cruntime.wasm

# supports up to two directories, add more '*/' elements in a wildcard to support deeper nesting
SRC := $(wildcard $(SRCDIR)/*.c) $(wildcard $(SRCDIR)/*/*.c) $(wildcard $(SRCDIR)/*/*/*.c)

OBJECT := $(patsubst $(SRCDIR)/%.c,$(OUTDIR)/%.o,$(SRC))
DEPENDENCY := $(OBJECT:.o=.d)

OBJECT_WASM := $(patsubst $(SRCDIR)/%.c,$(OUTDIR_WASM)/%.o,$(SRC))
DEPENDENCY_WASM := $(OBJECT_WASM:.o=.d)

#########
# Rules #
#########

.PHONY: all
all: $(BIN)

.PHONY: compilation-database
compilation-database:
# pass -DMPY_WASM to compiler
# so that the language server does not ignore
# the WASM only code parts
	CFLAGS=-DMPY_WASM bear -- $(MAKE) clean all

ifneq ($(MAKECMDGOALS),clean)
$(DEPENDENCY) : $(OUTDIR)/%.d: $(SRCDIR)/%.c
	$(if $(wildcard $(dir $@)),,$(MKDIR_P) $(dir $@))
	$(CC) $(CFLAGS) -MM $< > $@
-include $(DEPENDENCY)
$(DEPENDENCY_WASM) : $(OUTDIR_WASM)/%.d: $(SRCDIR)/%.c
	$(if $(wildcard $(dir $@)),,$(MKDIR_P) $(dir $@))
	-$(CC_WASM) $(CFLAGS) -MM $< > $@
-include $(DEPENDENCY_WASM)
endif

$(BIN): $(OBJECT)
	$(if $(wildcard $(dir $@)),,$(MKDIR_P) $(dir $@))
	$(CC) $(LDFLAGS) $^ $(LOADLIBS) $(LDLIBS) -o $@

.PHONY: lib_wasm
lib_wasm: $(LIB_WASM)

$(LIB_WASM): $(OBJECT_WASM)
	$(if $(wildcard $(dir $@)),,$(MKDIR_P) $(dir $@))
	$(CC_WASM) $(LDFLAGS_WASM)  $^ $(LOADLIBS_WASM) $(LDLIBS_WASM) -o $@

$(OBJECT) : $(OUTDIR)/%.o: $(SRCDIR)/%.c
	$(if $(wildcard $(dir $@)),,$(MKDIR_P) $(dir $@))
	$(CC) $(CPPFLAGS) $(CFLAGS) -c -o $@ $<

$(OBJECT_WASM) : $(OUTDIR_WASM)/%.o: $(SRCDIR)/%.c
	$(if $(wildcard $(dir $@)),,$(MKDIR_P) $(dir $@))
	$(CC_WASM) $(CFLAGS_WASM) -c -o $@ $<

.PHONY: run
run: $(BIN)
	./bin/program

.PHONY: clean
clean:
	$(RM) -r $(BIN) $(OBJECT) $(OBJECT_WASM) $(LIB_WASM) $(DEPENDENCY) $(DEPENDENCY_WASM) $(wildcard $(DOCDIR)/*)

.PHONY: doc
doc:
	$(if $(wildcard $(dir $@)),,$(MKDIR_P) $(dir $@))
	doxygen $(DOXYFILE)

.PHONY: lint
lint:
	clang-tidy --checks=*,-bugprone-reserved-identifier,-cert-dcl37-c,-cert-dcl51-cpp,-altera-struct-pack-align $(SRC)
