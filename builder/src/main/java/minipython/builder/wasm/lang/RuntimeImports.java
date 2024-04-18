package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Optional;

import static minipython.builder.wasm.lang.RuntimeImport.RuntimeImportType.I32;
import static minipython.builder.wasm.lang.RuntimeImport.RuntimeImportType.I64;

import minipython.builder.wasm.lang.RuntimeImport.RuntimeImportType;

/**
 * Instances of \a RuntimeImport for all c-runtime functions used
 * by code generation.
 *
 * NOTE(FW): this should be auto-generatable - but I haven't even tried yet
 */
public class RuntimeImports {

    public final static RuntimeImport MPY_BUILTINS_GET_FN_PRINT = new RuntimeImport("__mpy_builtins_get_fn_print", List.of(), Optional.of(I32));
    public final static RuntimeImport MPY_OBJ_REF_DEC = new RuntimeImport("__mpy_obj_ref_dec", List.of(new RuntimeImportType[]{I32}), Optional.empty());
    public final static RuntimeImport MPY_OBJ_REF_INC = new RuntimeImport("__mpy_obj_ref_inc", List.of(new RuntimeImportType[]{I32}), Optional.empty());
    public final static RuntimeImport MPY_OBJ_INIT_OBJECT = new RuntimeImport("__mpy_obj_init_object", List.of(new RuntimeImportType[]{}), Optional.of(I32));
    public final static RuntimeImport MPY_OBJ_INIT_INT = new RuntimeImport("__mpy_obj_init_int", List.of(new RuntimeImportType[]{I64}), Optional.of(I32));
    public final static RuntimeImport MPY_OBJ_INIT_BOOLEAN = new RuntimeImport("__mpy_obj_init_boolean", List.of(new RuntimeImportType[]{I32}), Optional.of(I32));
    public final static RuntimeImport MPY_TUPLE_ASSIGN = new RuntimeImport("__mpy_tuple_assign", List.of(new RuntimeImportType[]{I32, I32, I32}), Optional.of(I32));
    public final static RuntimeImport MPY_OBJ_INIT_TUPLE = new RuntimeImport("__mpy_obj_init_tuple", List.of(new RuntimeImportType[]{I32}), Optional.of(I32));
    public final static RuntimeImport MPY_CALL = new RuntimeImport("__mpy_call", List.of(new RuntimeImportType[]{I32, I32, I32}), Optional.of(I32));
    public final static RuntimeImport MPY_OBJ_INIT_STR_STATIC = new RuntimeImport("__mpy_obj_init_str_static", List.of(new RuntimeImportType[]{I32}), Optional.of(I32));
    public final static RuntimeImport MPY_STR_ALLOC = new RuntimeImport("__mpy_str_alloc", List.of(new RuntimeImportType[]{I32}), Optional.of(I32));
    public final static RuntimeImport MPY_STR_SET = new RuntimeImport("__mpy_str_set", List.of(new RuntimeImportType[]{I32, I32, I32}), Optional.empty());
    public final static RuntimeImport MPY_STR_INTO_CSTR = new RuntimeImport("__mpy_str_into_cstr", List.of(new RuntimeImportType[]{I32}), Optional.of(I32));
}
