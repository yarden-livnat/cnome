package org.mo.closure.v0;

/**
 * Closure.java
 * Contains closure templates for up to 20 type defined method parameters which should be more than enough. 
 * 
 * Copyright (C) 2012 Mohamed Seifeddine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, 
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Mohamed Seifeddine
**/
public final class Closure {
    // Cannot be initiated or subclassed. Works as a container class only
    private Closure() {};
    
    /** O = Returns Object and takes an unlimited amount of parameters of type Object */
    public static interface O  {
        public Object call(Object... objs);               
    }
    
    /** R0 = Return of defined type with 0 parameters */
    public static interface R0<Returns>  {
        public Returns call();
    }

    /** R1 = Return of defined type with 1 defined parameters */
    public static interface R1<Returns, Param1>  {
        public Returns call(Param1 p1);
    }

    /** R2 = Return of defined type with 2 defined parameters */
    public static interface R2<Returns, Param1, Param2>  {
        public Returns call(Param1 p1, Param2 p2);
    }

    /** R3 = Return of defined type with 3 defined parameters */
    public static interface R3<Returns, Param1, Param2, Param3>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3);
    }

    /** R4 = Return of defined type with 4 defined parameters */
    public static interface R4<Returns, Param1, Param2, Param3, Param4>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4);
    }

    /** R5 = Return of defined type with 5 defined parameters */
    public static interface R5<Returns, Param1, Param2, Param3, Param4, Param5>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5);
    }

    /** R6 = Return of defined type with 6 defined parameters */
    public static interface R6<Returns, Param1, Param2, Param3, Param4, Param5, Param6>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6);
    }

    /** R7 = Return of defined type with 7 defined parameters */
    public static interface R7<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7);
    }

    /** R8 = Return of defined type with 8 defined parameters */
    public static interface R8<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8);
    }

    /** R9 = Return of defined type with 9 defined parameters */
    public static interface R9<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9);
    }

    /** R10 = Return of defined type with 10 defined parameters */
    public static interface R10<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10);
    }

    /** R11 = Return of defined type with 11 defined parameters */
    public static interface R11<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11);
    }

    /** R12 = Return of defined type with 12 defined parameters */
    public static interface R12<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12);
    }

    /** R13 = Return of defined type with 13 defined parameters */
    public static interface R13<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13);
    }

    /** R14 = Return of defined type with 14 defined parameters */
    public static interface R14<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14);
    }

    /** R15 = Return of defined type with 15 defined parameters */
    public static interface R15<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15);
    }

    /** R16 = Return of defined type with 16 defined parameters */
    public static interface R16<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16);
    }

    /** R17 = Return of defined type with 17 defined parameters */
    public static interface R17<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17);
    }

    /** R18 = Return of defined type with 18 defined parameters */
    public static interface R18<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18);
    }

    /** R19 = Return of defined type with 19 defined parameters */
    public static interface R19<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18, Param19>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18, Param19 p19);
    }

    /** R20 = Return of defined type with 20 defined parameters */
    public static interface R20<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18, Param19, Param20>  {
        public Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18, Param19 p19, Param20 p20);
    }

    /** RU = Return of defined type with undefined and unlimited amount of method parameters */
    public static interface RU<Returns>  {
        public Returns call(Object... parameters );
    }

    //======================================================================================================
    // Voids are treated specially as well to cover the complete spectrum for static declaration and usage
    // Question: Should we allow
    //======================================================================================================


    /** V0 = Void with zero parameters */
    public static interface V0  {
        public void call();
    }

    /** V1 = Void with 1 defined parameter */
    public static interface V1<Param1>  {
        public void call(Param1 p1);
    }

    /** V2 = Void with 2 defined parameters */
    public static interface V2<Param1, Param2>  {
        public void call(Param1 p1, Param2 p2);
    }

    /** V3 = Void with 3 defined parameters */
    public static interface V3<Param1, Param2, Param3>  {
        public void call(Param1 p1, Param2 p2, Param3 p3);
    }

    /** V4 = Void with 4 defined parameters */
    public static interface V4<Param1, Param2, Param3, Param4>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4);
    }

    /** V5 = Void with 5 defined parameters */
    public static interface V5<Param1, Param2, Param3, Param4, Param5>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5);
    }

    /** V6 = Void with 6 defined parameters */
    public static interface V6<Param1, Param2, Param3, Param4, Param5, Param6>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6);
    }

    /** V7 = Void with 7 defined parameters */
    public static interface V7<Param1, Param2, Param3, Param4, Param5, Param6, Param7>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7);
    }

    /** V8 = Void with 8 defined parameters */
    public static interface V8<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8);
    }

    /** V9 = Void with 9 defined parameters */
    public static interface V9<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9);
    }

    /** V10 = Void with 10 defined parameters */
    public static interface V10<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10);
    }

    /** V11 = Void with 11 defined parameters */
    public static interface V11<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11);
    }

    /** V12 = Void with 12 defined parameters */
    public static interface V12<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12);
    }

    /** V13 = Void with 13 defined parameters */
    public static interface V13<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13);
    }

    /** V14 = Void with 14 defined parameters */
    public static interface V14<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14);
    }

    /** V15 = Void with 15 defined parameters */
    public static interface V15<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15);
    }

    /** V16 = Void with 16 defined parameters */
    public static interface V16<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16);
    }

    /** V17 = Void with 17 defined parameters */
    public static interface V17<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17);
    }

    /** V18 = Void with 18 defined parameters */
    public static interface V18<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18);
    }

    /** V19 = Void with 19 defined parameters */
    public static interface V19<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18, Param19>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18, Param19 p19);
    }

    /** V20 = Void with 20 defined parameters */
    public static interface V20<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18, Param19, Param20>  {
        public void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18, Param19 p19, Param20 p20);
    }

    /** VU = Void with undefined and unlimited amount of method parameters */
    public static interface VU  {
        public void call(Object... parameters);
    }
    
}
