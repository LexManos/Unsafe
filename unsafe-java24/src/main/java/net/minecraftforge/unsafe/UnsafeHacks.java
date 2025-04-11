/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;

import sun.misc.Unsafe;

public final class UnsafeHacks {
    private static final MethodHandles.Lookup LOOKUP = getLookup();
    private static final Unsafe UNSAFE = getUnsafe();

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return cast(UNSAFE.allocateInstance(clazz));
        } catch (InstantiationException e) {
            return sneak(e);
        }
    }

    public static <T> T getField(Field field, Object instance) {
        return null; //cast(UNSAFE.getObject(instance, UNSAFE.objectFieldOffset(field)));
    }

    public static void setField(Field field, Object instance, Object value) {
        //UNSAFE.putObject(instance, UNSAFE.objectFieldOffset(field), value);
    }

    public static int getIntField(Field field, Object instance) {
        return 0;//UNSAFE.getInt(instance, UNSAFE.objectFieldOffset(field));
    }

    public static void setIntField(Field field, Object instance, int value) {
        //UNSAFE.putInt(instance, UNSAFE.objectFieldOffset(field), value);
    }

    public static <O, T> UnsafeFieldAccess<O, T> findField(Class<O> clazz, String name) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getName().equals(name)) {
                try {
                    return new UnsafeFieldAccess<O, T>(LOOKUP, f);
                } catch (IllegalAccessException e) {
                    return sneak(e);
                }
            }
        }
        return null;
    }

    public static <O> UnsafeFieldAccess.Int<O> findIntField(Class<O> clazz, String name) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getName().equals(name)) {
                try {
                    return new UnsafeFieldAccess.Int<O>(LOOKUP, f);
                } catch (IllegalAccessException e) {
                    return sneak(e);
                }
            }
        }
        return null;
    }

    public static <O> UnsafeFieldAccess.Bool<O> findBooleanField(Class<O> clazz, String name) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getName().equals(name)) {
                try {
                    return new UnsafeFieldAccess.Bool<O>(LOOKUP, f);
                } catch (IllegalAccessException e) {
                    return sneak(e);
                }
            }
        }
        return null;
    }

    public static void setAccessible(AccessibleObject target) {
        //SETACCESSIBLE.accept(target);
    }


    /*=======================================================================
     *=======================================================================
     */
    private static MethodHandles.Lookup getLookupReflect() {
        try {
            var field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            field.setAccessible(true);
            return (MethodHandles.Lookup)field.get(null);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalArgumentException | IllegalAccessException | InaccessibleObjectException e) {
            // This is expected if we don't have --add-opens java.base/java.lang.invoke=net.minecraftforge.unsafe
            return null;
        }
    }

    private static MethodHandles.Lookup getLookup() {
        var lookup = getLookupReflect();
        // If we can reflect it, then the java.base module is open so lets be happy
        if (lookup != null)
            return lookup;

        // TODO: [UnSafe] Probably should throw this as an exception in the static constructor, delay it until someone tries to use Unsafe
        throw new IllegalStateException("Could not get trusted MethodHandles.Lookup, " +
            "Relaunch the java process with " +
            "--add-opens java.base/java.lang.invoke=net.minecraftforge.unsafe");
    }

    private static Unsafe getUnsafe() {
        UnsafeFieldAccess<Unsafe, Unsafe> field = findField(Unsafe.class, "theUnsafe");
        return field.get(null);
    }

    @SuppressWarnings("unchecked")
    static <E extends Throwable, R> R sneak(Throwable e) throws E {
        throw (E)e;
    }

    @SuppressWarnings("unchecked")
    static <T> T cast(Object inst) {
        return (T)inst;
    }
}
