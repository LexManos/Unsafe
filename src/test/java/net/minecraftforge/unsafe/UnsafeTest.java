/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.unsafe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class UnsafeTest {
    @Test
    public void canGetUnsafe() {
        assertNotNull(UnsafeHacks.theUnsafe());
    }

    @Test
    public void canSetPrivate() {
        List<?> arr = Arrays.asList();

        @SuppressWarnings("unchecked")
        UnsafeFieldAccess<List<?>, Object[]> accessor = UnsafeHacks.findField((Class<List<?>>)arr.getClass(), "a");

        assertNotNull(accessor, "Failed to find ArrayList.a");
        assertTrue(arr.isEmpty(), "New ArrayList was not empty");

        accessor.set(arr, new Object[] { arr });

        assertEquals(arr.get(0), arr, "ArrayList private value didn't set correctly");
    }

    @Test
    public void canGetPrivate() {
        Object value = new Object();
        List<?> arr = Arrays.asList(value);

        @SuppressWarnings("unchecked")
        UnsafeFieldAccess<List<?>, Object[]> accessor = UnsafeHacks.findField((Class<List<?>>)arr.getClass(), "a");

        assertNotNull(accessor, "Failed to find ArrayList.a");
        assertEquals(1, arr.size(), "New ArrayList size");

        Object[] internal = accessor.get(arr);
        assertNotNull(internal, "Failed to get internal value");
        assertEquals(1, internal.length, "Internal value wrong size");
        assertEquals(value, internal[0], "Invalid internal value[0]");
    }
}
