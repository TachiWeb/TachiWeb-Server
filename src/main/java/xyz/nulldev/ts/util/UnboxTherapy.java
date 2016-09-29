/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.nulldev.ts.util;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 11/07/16
 *
 * What happens when nulldev gets pissed off at Java
 */
public class UnboxTherapy {
    /**
     * Unbox an Integer defaulting to -1 if it is null.
     * @param integer The integer to unbox.
     * @return The unboxed integer or -1 if the integer was null.
     */
    public static int unbox(Integer integer) {
        return integer != null ? integer : -1;
    }

    /**
     * Unbox a Long defaulting to -1 if it is null.
     * @param longNum The long to unbox.
     * @return The unboxed long or -1 if the long was null.
     */
    public static long unbox(Long longNum) {
        return longNum != null ? longNum : -1;
    }
}
