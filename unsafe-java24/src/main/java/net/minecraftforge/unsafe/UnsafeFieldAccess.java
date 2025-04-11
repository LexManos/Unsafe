/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public class UnsafeFieldAccess<Owner, Type> {
    protected final VarHandle varHandle;

    UnsafeFieldAccess(MethodHandles.Lookup lookup, Field field) throws IllegalAccessException {
        varHandle = lookup.unreflectVarHandle(field);
    }


    public <I extends Owner> Type get(I instance) {
        return UnsafeHacks.cast(instance == null ? varHandle.get() : varHandle.get(instance));
    }

    public <I extends Owner> void set(I instance, Type value) {
        if (instance == null)
            varHandle.set(value);
        else
            varHandle.set(instance, value);
    }

    public static class Int<Owner> extends UnsafeFieldAccess<Owner, Integer> {
        Int(MethodHandles.Lookup lookup, Field field) throws IllegalAccessException {
            super(lookup, field);
        }

        public <I extends Owner> int getInt(I instance) {
           var ret = get(instance);
           return ret == null ? -1 : ret;
        }

        @Override
        public <I extends Owner> void set(I instance, Integer value) {
            setInt(instance, value);
        }

        public <I extends Owner> void setInt(I instance, int value) {
            if (instance == null)
                varHandle.set(value);
            else
                varHandle.set(instance, value);
        }
    }

    public static class Bool<Owner> extends UnsafeFieldAccess<Owner, Boolean> {
        Bool(MethodHandles.Lookup lookup, Field field) throws IllegalAccessException {
            super(lookup, field);
        }

        public <I extends Owner> boolean getBoolean(I instance) {
            var ret = get(instance);
            return ret == null ? false : ret;
        }

        @Override
        public <I extends Owner> void set(I instance, Boolean value) {
            setBoolean(instance, value);
        }

        public <I extends Owner> void setBoolean(I instance, boolean value) {
            if (instance == null)
                varHandle.set(value);
            else
                varHandle.set(instance, value);
        }
    }
}
