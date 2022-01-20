package jnr.ffi.struct;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.TstUtil;
import jnr.ffi.struct.NumericStructTest.NumericStruct;
import jnr.ffi.struct.NumericUnionTest.NumericUnion;
import jnr.ffi.types.u_int64_t;
import jnr.ffi.types.u_int8_t;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests NestedStruct.c
 * This tests a struct that contains an inner struct, an inner union,
 * a pointer to a struct and a pointer to a union.
 */
@SuppressWarnings("RedundantCast")
public class NestedStructTest {

    private static final int ITERATIONS_COUNT = 100_000;

    // ThreadLocalRandom instead of Random because we want a nextLong() method with bounds which Random doesn't have
    private static final ThreadLocalRandom R = ThreadLocalRandom.current();

    private static Lib lib;
    private static Runtime runtime;

    public static class NestedStruct extends Struct {
        public final NumericStruct inner_NumericStruct = inner(NumericStruct.class);
        public final NumericUnion inner_NumericUnion = inner(NumericUnion.class);

        public final StructRef<NumericStruct> ptr_NumericStruct = new StructRef<>(NumericStruct.class);
        public final StructRef<NumericUnion> ptr_NumericUnion = new StructRef<>(NumericUnion.class);

        public NestedStruct(Runtime runtime) {super(runtime);}

        public NestedStruct() {this(runtime);}
    }

    public static interface Lib {
        // ========================= Inner Struct =======================================

        public byte nested_struct_inner_struct_get_int8_t(NestedStruct s);
        public void nested_struct_inner_struct_set_int8_t(NestedStruct s, byte v);

        public long nested_struct_inner_struct_get_int64_t(NestedStruct s);
        public void nested_struct_inner_struct_set_int64_t(NestedStruct s, long v);

        public float nested_struct_inner_struct_get_float(NestedStruct s);
        public void nested_struct_inner_struct_set_float(NestedStruct s, float v);

        public double nested_struct_inner_struct_get_double(NestedStruct s);
        public void nested_struct_inner_struct_set_double(NestedStruct s, double v);

        public Pointer nested_struct_inner_struct_get_pointer(NestedStruct s);
        public void nested_struct_inner_struct_set_pointer(NestedStruct s, Pointer v);

        // ========================= Pointer Struct =====================================

        public byte nested_struct_ptr_struct_get_int8_t(NestedStruct s);
        public void nested_struct_ptr_struct_set_int8_t(NestedStruct s, byte v);

        public long nested_struct_ptr_struct_get_int64_t(NestedStruct s);
        public void nested_struct_ptr_struct_set_int64_t(NestedStruct s, long v);

        public float nested_struct_ptr_struct_get_float(NestedStruct s);
        public void nested_struct_ptr_struct_set_float(NestedStruct s, float v);

        public double nested_struct_ptr_struct_get_double(NestedStruct s);
        public void nested_struct_ptr_struct_set_double(NestedStruct s, double v);

        public Pointer nested_struct_ptr_struct_get_pointer(NestedStruct s);
        public void nested_struct_ptr_struct_set_pointer(NestedStruct s, Pointer v);

        // ========================= Inner Union ========================================

        public byte nested_struct_inner_union_get_int8_t(NestedStruct s);
        public void nested_struct_inner_union_set_int8_t(NestedStruct s, byte v);

        public long nested_struct_inner_union_get_int64_t(NestedStruct s);
        public void nested_struct_inner_union_set_int64_t(NestedStruct s, long v);

        public float nested_struct_inner_union_get_float(NestedStruct s);
        public void nested_struct_inner_union_set_float(NestedStruct s, float v);

        public double nested_struct_inner_union_get_double(NestedStruct s);
        public void nested_struct_inner_union_set_double(NestedStruct s, double v);

        public Pointer nested_struct_inner_union_get_pointer(NestedStruct s);
        public void nested_struct_inner_union_set_pointer(NestedStruct s, Pointer v);

        // ========================= Pointer Union ======================================

        public byte nested_struct_ptr_union_get_int8_t(NestedStruct s);
        public void nested_struct_ptr_union_set_int8_t(NestedStruct s, byte v);

        public long nested_struct_ptr_union_get_int64_t(NestedStruct s);
        public void nested_struct_ptr_union_set_int64_t(NestedStruct s, long v);

        public float nested_struct_ptr_union_get_float(NestedStruct s);
        public void nested_struct_ptr_union_set_float(NestedStruct s, float v);

        public double nested_struct_ptr_union_get_double(NestedStruct s);
        public void nested_struct_ptr_union_set_double(NestedStruct s, double v);

        public Pointer nested_struct_ptr_union_get_pointer(NestedStruct s);
        public void nested_struct_ptr_union_set_pointer(NestedStruct s, Pointer v);

        public int nested_struct_size();
    }

    @BeforeAll
    public static void beforeAll() {
        lib = TstUtil.loadTestLib(Lib.class);
        runtime = Runtime.getRuntime(lib);
    }

    // ========================= Inner Struct =======================

    @Test
    public void testInnerStructByte() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((byte) 0, struct.inner_NumericStruct.val_int8_t.get());

            byte value = (byte) R.nextInt();
            struct.inner_NumericStruct.val_int8_t.set(value);

            assertEquals((byte) value, struct.inner_NumericStruct.val_int8_t.get(),
                    "Incorrect byte value in inner struct");
            assertEquals((byte) value, lib.nested_struct_inner_struct_get_int8_t(struct),
                    "Incorrect byte value in inner struct");

            value = (byte) R.nextInt();
            lib.nested_struct_inner_struct_set_int8_t(struct, value);

            assertEquals((byte) value, struct.inner_NumericStruct.val_int8_t.get(),
                    "Incorrect byte value in inner struct");
            assertEquals((byte) value, lib.nested_struct_inner_struct_get_int8_t(struct),
                    "Incorrect byte value in inner struct");

            struct.inner_NumericStruct.reset();
        }
    }

    @Test
    public void testInnerStructLong() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((long) 0, struct.inner_NumericStruct.val_int64_t.get());

            long value = (long) R.nextLong();
            struct.inner_NumericStruct.val_int64_t.set(value);

            assertEquals((long) value, struct.inner_NumericStruct.val_int64_t.get(),
                    "Incorrect long value in inner struct");
            assertEquals((long) value, lib.nested_struct_inner_struct_get_int64_t(struct),
                    "Incorrect long value in inner struct");

            value = (long) R.nextLong();
            lib.nested_struct_inner_struct_set_int64_t(struct, value);

            assertEquals((long) value, struct.inner_NumericStruct.val_int64_t.get(),
                    "Incorrect long value in inner struct");
            assertEquals((long) value, lib.nested_struct_inner_struct_get_int64_t(struct),
                    "Incorrect long value in inner struct");

            struct.inner_NumericStruct.reset();
        }
    }

    @Test
    public void testInnerStructFloat() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((float) 0, struct.inner_NumericStruct.val_float.get());

            float value = (float) R.nextFloat();
            struct.inner_NumericStruct.val_float.set(value);

            assertEquals((float) value, struct.inner_NumericStruct.val_float.get(),
                    "Incorrect float value in inner struct");
            assertEquals((float) value, lib.nested_struct_inner_struct_get_float(struct),
                    "Incorrect float value in inner struct");

            value = (float) R.nextFloat();
            lib.nested_struct_inner_struct_set_float(struct, value);

            assertEquals((float) value, struct.inner_NumericStruct.val_float.get(),
                    "Incorrect float value in inner struct");
            assertEquals((float) value, lib.nested_struct_inner_struct_get_float(struct),
                    "Incorrect float value in inner struct");

            struct.inner_NumericStruct.reset();
        }
    }

    @Test
    public void testInnerStructDouble() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((double) 0, struct.inner_NumericStruct.val_double.get());

            double value = (double) R.nextDouble();
            struct.inner_NumericStruct.val_double.set(value);

            assertEquals((double) value, struct.inner_NumericStruct.val_double.get(),
                    "Incorrect double value in inner struct");
            assertEquals((double) value, lib.nested_struct_inner_struct_get_double(struct),
                    "Incorrect double value in inner struct");

            value = (double) R.nextDouble();
            lib.nested_struct_inner_struct_set_double(struct, value);

            assertEquals((double) value, struct.inner_NumericStruct.val_double.get(),
                    "Incorrect double value in inner struct");
            assertEquals((double) value, lib.nested_struct_inner_struct_get_double(struct),
                    "Incorrect double value in inner struct");

            struct.inner_NumericStruct.reset();
        }
    }

    @Test
    public void testInnerStructPointer() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertNull(struct.inner_NumericStruct.val_pointer.get());

            long value = (long) R.nextLong(Long.MAX_VALUE);
            struct.inner_NumericStruct.val_pointer.set(value);

            assertEquals((long) value, struct.inner_NumericStruct.val_pointer.get().address(),
                    "Incorrect pointer address in inner struct");
            assertEquals((long) value, lib.nested_struct_inner_struct_get_pointer(struct).address(),
                    "Incorrect pointer address in inner struct");

            value = (long) R.nextLong(Long.MAX_VALUE);
            lib.nested_struct_inner_struct_set_pointer(struct, Pointer.wrap(runtime, value));

            assertEquals((long) value, struct.inner_NumericStruct.val_pointer.get().address(),
                    "Incorrect pointer address in inner struct");
            assertEquals((long) value, lib.nested_struct_inner_struct_get_pointer(struct).address(),
                    "Incorrect pointer address value in inner struct");

            struct.inner_NumericStruct.reset();
        }
    }

    // ========================= Pointer Struct =====================

    // TODO: 19-Jan-2022 @basshelal: StructRef doesn't use direct memory by default meaning directly getting the
    //  backed struct and changing it doesn't do anything, we need to set the structRef's backing pointer to a
    //  direct pointer from Memory.allocateDirect()
    //  this:
    //  struct.ptr_NumericStruct.get().val_int8_t.set(69);
    //  struct.ptr_NumericStruct.get().val_int8_t.get() == 69; // fails without above Memory.allocateDirect()
    //  because everytime get(): Struct is called an entirely new struct is created on the JVM and it's backing memory
    //  isn't direct memory by default.
    //  I tried fixing this in different ways in Struct but all failed for different reasons,
    //  the code is too messy to fix this and too many changes would have to be made to make this work
    //  This is something we need to fix (or at worst document) but only after we have a good test suite to ensure that
    //  any changes made to fixing this won't break something else

    @Test
    public void testPointerStructByte() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericStruct.set(Memory.allocateDirect(runtime, Struct.size(NumericStruct.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((byte) 0, struct.ptr_NumericStruct.get().val_int8_t.get());

            byte value = (byte) R.nextInt();
            struct.ptr_NumericStruct.get().val_int8_t.set(value);

            assertEquals((byte) value, struct.ptr_NumericStruct.get().val_int8_t.get(),
                    "Incorrect byte value in pointer struct");
            assertEquals((byte) value, lib.nested_struct_ptr_struct_get_int8_t(struct),
                    "Incorrect byte value in pointer struct");

            value = (byte) R.nextInt();
            lib.nested_struct_ptr_struct_set_int8_t(struct, value);

            assertEquals((byte) value, struct.ptr_NumericStruct.get().val_int8_t.get(),
                    "Incorrect byte value in pointer struct");
            assertEquals((byte) value, lib.nested_struct_ptr_struct_get_int8_t(struct),
                    "Incorrect byte value in pointer struct");

            struct.ptr_NumericStruct.get().reset();
        }
    }

    @Test
    public void testPointerStructLong() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericStruct.set(Memory.allocateDirect(runtime, Struct.size(NumericStruct.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((long) 0, struct.ptr_NumericStruct.get().val_int64_t.get());

            long value = (long) R.nextLong();
            struct.ptr_NumericStruct.get().val_int64_t.set(value);

            assertEquals((long) value, struct.ptr_NumericStruct.get().val_int64_t.get(),
                    "Incorrect long value in pointer struct");
            assertEquals((long) value, lib.nested_struct_ptr_struct_get_int64_t(struct),
                    "Incorrect long value in pointer struct");

            value = (long) R.nextLong();
            lib.nested_struct_ptr_struct_set_int64_t(struct, value);

            assertEquals((long) value, struct.ptr_NumericStruct.get().val_int64_t.get(),
                    "Incorrect long value in pointer struct");
            assertEquals((long) value, lib.nested_struct_ptr_struct_get_int64_t(struct),
                    "Incorrect long value in pointer struct");

            struct.ptr_NumericStruct.get().reset();
        }
    }

    @Test
    public void testPointerStructFloat() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericStruct.set(Memory.allocateDirect(runtime, Struct.size(NumericStruct.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((float) 0, struct.ptr_NumericStruct.get().val_float.get());

            float value = (float) R.nextFloat();
            struct.ptr_NumericStruct.get().val_float.set(value);

            assertEquals((float) value, struct.ptr_NumericStruct.get().val_float.get(),
                    "Incorrect float value in pointer struct");
            assertEquals((float) value, lib.nested_struct_ptr_struct_get_float(struct),
                    "Incorrect float value in pointer struct");

            value = (float) R.nextFloat();
            lib.nested_struct_ptr_struct_set_float(struct, value);

            assertEquals((float) value, struct.ptr_NumericStruct.get().val_float.get(),
                    "Incorrect float value in pointer struct");
            assertEquals((float) value, lib.nested_struct_ptr_struct_get_float(struct),
                    "Incorrect float value in pointer struct");

            struct.ptr_NumericStruct.get().reset();
        }
    }

    @Test
    public void testPointerStructDouble() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericStruct.set(Memory.allocateDirect(runtime, Struct.size(NumericStruct.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((double) 0, struct.ptr_NumericStruct.get().val_double.get());

            double value = (double) R.nextDouble();
            struct.ptr_NumericStruct.get().val_double.set(value);

            assertEquals((double) value, struct.ptr_NumericStruct.get().val_double.get(),
                    "Incorrect double value in pointer struct");
            assertEquals((double) value, lib.nested_struct_ptr_struct_get_double(struct),
                    "Incorrect double value in pointer struct");

            value = (double) R.nextDouble();
            lib.nested_struct_ptr_struct_set_double(struct, value);

            assertEquals((double) value, struct.ptr_NumericStruct.get().val_double.get(),
                    "Incorrect double value in pointer struct");
            assertEquals((double) value, lib.nested_struct_ptr_struct_get_double(struct),
                    "Incorrect double value in pointer struct");

            struct.ptr_NumericStruct.get().reset();
        }
    }

    @Test
    public void testPointerStructPointer() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericStruct.set(Memory.allocateDirect(runtime, Struct.size(NumericStruct.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertNull(struct.ptr_NumericStruct.get().val_pointer.get());

            long value = (long) R.nextLong(Long.MAX_VALUE);
            struct.ptr_NumericStruct.get().val_pointer.set(value);

            assertEquals((long) value, struct.ptr_NumericStruct.get().val_pointer.get().address(),
                    "Incorrect pointer address in pointer struct");
            assertEquals((long) value, lib.nested_struct_ptr_struct_get_pointer(struct).address(),
                    "Incorrect pointer address in pointer struct");

            value = (long) R.nextLong(Long.MAX_VALUE);
            lib.nested_struct_ptr_struct_set_pointer(struct, Pointer.wrap(runtime, value));

            assertEquals((long) value, struct.ptr_NumericStruct.get().val_pointer.get().address(),
                    "Incorrect pointer address in pointer struct");
            assertEquals((long) value, lib.nested_struct_ptr_struct_get_pointer(struct).address(),
                    "Incorrect pointer address value in pointer struct");

            struct.ptr_NumericStruct.get().reset();
        }
    }

    // ========================= Inner Union ========================

    @Test
    public void testInnerUnionByte() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((byte) 0, struct.inner_NumericUnion.val_int8_t.get());

            byte value = (byte) R.nextInt();
            struct.inner_NumericUnion.val_int8_t.set(value);

            assertEquals((byte) value, struct.inner_NumericUnion.val_int8_t.get(),
                    "Incorrect byte value in inner union");
            assertEquals((byte) value, lib.nested_struct_inner_union_get_int8_t(struct),
                    "Incorrect byte value in inner union");

            value = (byte) R.nextInt();
            lib.nested_struct_inner_union_set_int8_t(struct, value);

            assertEquals((byte) value, struct.inner_NumericUnion.val_int8_t.get(),
                    "Incorrect byte value in inner union");
            assertEquals((byte) value, lib.nested_struct_inner_union_get_int8_t(struct),
                    "Incorrect byte value in inner union");

            struct.inner_NumericUnion.reset();
        }
    }

    @Test
    public void testInnerUnionLong() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((long) 0, struct.inner_NumericUnion.val_int64_t.get());

            long value = (long) R.nextLong();
            struct.inner_NumericUnion.val_int64_t.set(value);

            assertEquals((long) value, struct.inner_NumericUnion.val_int64_t.get(),
                    "Incorrect long value in inner union");
            assertEquals((long) value, lib.nested_struct_inner_union_get_int64_t(struct),
                    "Incorrect long value in inner union");

            value = (long) R.nextLong();
            lib.nested_struct_inner_union_set_int64_t(struct, value);

            assertEquals((long) value, struct.inner_NumericUnion.val_int64_t.get(),
                    "Incorrect long value in inner union");
            assertEquals((long) value, lib.nested_struct_inner_union_get_int64_t(struct),
                    "Incorrect long value in inner union");

            struct.inner_NumericUnion.reset();
        }
    }

    @Test
    public void testInnerUnionFloat() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((float) 0, struct.inner_NumericUnion.val_float.get());

            float value = (float) R.nextFloat();
            struct.inner_NumericUnion.val_float.set(value);

            assertEquals((float) value, struct.inner_NumericUnion.val_float.get(),
                    "Incorrect float value in inner union");
            assertEquals((float) value, lib.nested_struct_inner_union_get_float(struct),
                    "Incorrect float value in inner union");

            value = (float) R.nextFloat();
            lib.nested_struct_inner_union_set_float(struct, value);

            assertEquals((float) value, struct.inner_NumericUnion.val_float.get(),
                    "Incorrect float value in inner union");
            assertEquals((float) value, lib.nested_struct_inner_union_get_float(struct),
                    "Incorrect float value in inner union");

            struct.inner_NumericUnion.reset();
        }
    }

    @Test
    public void testInnerUnionDouble() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((double) 0, struct.inner_NumericUnion.val_double.get());

            double value = (double) R.nextDouble();
            struct.inner_NumericUnion.val_double.set(value);

            assertEquals((double) value, struct.inner_NumericUnion.val_double.get(),
                    "Incorrect double value in inner union");
            assertEquals((double) value, lib.nested_struct_inner_union_get_double(struct),
                    "Incorrect double value in inner union");

            value = (double) R.nextDouble();
            lib.nested_struct_inner_union_set_double(struct, value);

            assertEquals((double) value, struct.inner_NumericUnion.val_double.get(),
                    "Incorrect double value in inner union");
            assertEquals((double) value, lib.nested_struct_inner_union_get_double(struct),
                    "Incorrect double value in inner union");

            struct.inner_NumericUnion.reset();
        }
    }

    @Test
    public void testInnerUnionPointer() {
        NestedStruct struct = new NestedStruct();
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertNull(struct.inner_NumericUnion.val_pointer.get());

            long value = (long) R.nextLong(Long.MAX_VALUE);
            struct.inner_NumericUnion.val_pointer.set(value);

            assertEquals((long) value, struct.inner_NumericUnion.val_pointer.get().address(),
                    "Incorrect pointer address in inner union");
            assertEquals((long) value, lib.nested_struct_inner_union_get_pointer(struct).address(),
                    "Incorrect pointer address in inner union");

            value = (long) R.nextLong(Long.MAX_VALUE);
            lib.nested_struct_inner_union_set_pointer(struct, Pointer.wrap(runtime, value));

            assertEquals((long) value, struct.inner_NumericUnion.val_pointer.get().address(),
                    "Incorrect pointer address in inner union");
            assertEquals((long) value, lib.nested_struct_inner_union_get_pointer(struct).address(),
                    "Incorrect pointer address value in inner union");

            struct.inner_NumericUnion.reset();
        }
    }

    // ========================= Pointer Union ======================

    @Test
    public void testPointerUnionByte() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericUnion.set(Memory.allocateDirect(runtime, Struct.size(NumericUnion.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((byte) 0, struct.ptr_NumericUnion.get().val_int8_t.get());

            byte value = (byte) R.nextInt();
            struct.ptr_NumericUnion.get().val_int8_t.set(value);

            assertEquals((byte) value, struct.ptr_NumericUnion.get().val_int8_t.get(),
                    "Incorrect byte value in pointer union");
            assertEquals((byte) value, lib.nested_struct_ptr_union_get_int8_t(struct),
                    "Incorrect byte value in pointer union");

            value = (byte) R.nextInt();
            lib.nested_struct_ptr_union_set_int8_t(struct, value);

            assertEquals((byte) value, struct.ptr_NumericUnion.get().val_int8_t.get(),
                    "Incorrect byte value in pointer union");
            assertEquals((byte) value, lib.nested_struct_ptr_union_get_int8_t(struct),
                    "Incorrect byte value in pointer union");

            struct.ptr_NumericUnion.get().reset();
        }
    }

    @Test
    public void testPointerUnionLong() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericUnion.set(Memory.allocateDirect(runtime, Struct.size(NumericUnion.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((long) 0, struct.ptr_NumericUnion.get().val_int64_t.get());

            long value = (long) R.nextLong();
            struct.ptr_NumericUnion.get().val_int64_t.set(value);

            assertEquals((long) value, struct.ptr_NumericUnion.get().val_int64_t.get(),
                    "Incorrect long value in pointer union");
            assertEquals((long) value, lib.nested_struct_ptr_union_get_int64_t(struct),
                    "Incorrect long value in pointer union");

            value = (long) R.nextLong();
            lib.nested_struct_ptr_union_set_int64_t(struct, value);

            assertEquals((long) value, struct.ptr_NumericUnion.get().val_int64_t.get(),
                    "Incorrect long value in pointer union");
            assertEquals((long) value, lib.nested_struct_ptr_union_get_int64_t(struct),
                    "Incorrect long value in pointer union");

            struct.ptr_NumericUnion.get().reset();
        }
    }

    @Test
    public void testPointerUnionFloat() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericUnion.set(Memory.allocateDirect(runtime, Struct.size(NumericUnion.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((float) 0, struct.ptr_NumericUnion.get().val_float.get());

            float value = (float) R.nextFloat();
            struct.ptr_NumericUnion.get().val_float.set(value);

            assertEquals((float) value, struct.ptr_NumericUnion.get().val_float.get(),
                    "Incorrect float value in pointer union");
            assertEquals((float) value, lib.nested_struct_ptr_union_get_float(struct),
                    "Incorrect float value in pointer union");

            value = (float) R.nextFloat();
            lib.nested_struct_ptr_union_set_float(struct, value);

            assertEquals((float) value, struct.ptr_NumericUnion.get().val_float.get(),
                    "Incorrect float value in pointer union");
            assertEquals((float) value, lib.nested_struct_ptr_union_get_float(struct),
                    "Incorrect float value in pointer union");

            struct.ptr_NumericUnion.get().reset();
        }
    }

    @Test
    public void testPointerUnionDouble() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericUnion.set(Memory.allocateDirect(runtime, Struct.size(NumericUnion.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertEquals((double) 0, struct.ptr_NumericUnion.get().val_double.get());

            double value = (double) R.nextDouble();
            struct.ptr_NumericUnion.get().val_double.set(value);

            assertEquals((double) value, struct.ptr_NumericUnion.get().val_double.get(),
                    "Incorrect double value in pointer union");
            assertEquals((double) value, lib.nested_struct_ptr_union_get_double(struct),
                    "Incorrect double value in pointer union");

            value = (double) R.nextDouble();
            lib.nested_struct_ptr_union_set_double(struct, value);

            assertEquals((double) value, struct.ptr_NumericUnion.get().val_double.get(),
                    "Incorrect double value in pointer union");
            assertEquals((double) value, lib.nested_struct_ptr_union_get_double(struct),
                    "Incorrect double value in pointer union");

            struct.ptr_NumericUnion.get().reset();
        }
    }

    @Test
    public void testPointerUnionPointer() {
        NestedStruct struct = new NestedStruct();
        struct.ptr_NumericUnion.set(Memory.allocateDirect(runtime, Struct.size(NumericUnion.class)));
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            assertNull(struct.ptr_NumericUnion.get().val_pointer.get());

            long value = (long) R.nextLong(Long.MAX_VALUE);
            struct.ptr_NumericUnion.get().val_pointer.set(value);

            assertEquals((long) value, struct.ptr_NumericUnion.get().val_pointer.get().address(),
                    "Incorrect pointer address in pointer union");
            assertEquals((long) value, lib.nested_struct_ptr_union_get_pointer(struct).address(),
                    "Incorrect pointer address in pointer union");

            value = (long) R.nextLong(Long.MAX_VALUE);
            lib.nested_struct_ptr_union_set_pointer(struct, Pointer.wrap(runtime, value));

            assertEquals((long) value, struct.ptr_NumericUnion.get().val_pointer.get().address(),
                    "Incorrect pointer address in pointer union");
            assertEquals((long) value, lib.nested_struct_ptr_union_get_pointer(struct).address(),
                    "Incorrect pointer address value in pointer union");

            struct.ptr_NumericUnion.get().reset();
        }
    }

    // ========================= Other ==============================

    @Test
    public void testSize() {
        NestedStruct struct = new NestedStruct();
        assertEquals(Struct.size(struct), lib.nested_struct_size(),
                "Incorrect struct size");
    }
}
