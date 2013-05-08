package org.mo.closure.v1;

/**
 * Closure.java
 * Contains closure templates for up to 20 type defined method parameters which should be more than enough.
 * With Break functionality in Convience class. No extending between Closures. 
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

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    // Closure classes STARTS here
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    
    /** O = Returns Object and takes an unlimited amount of parameters of type Object */
    public static abstract class O extends ClosureConvience {
        public abstract Object call(Object... objs);
        
        /** {@inheritConstructorDoc} */
        protected O(Object... constructorArgs) { super(constructorArgs); }
    }
	
	/** R0 = Return of defined type with 0 parameters */
    public abstract static class R0<Returns> extends ClosureConvience {
        public abstract Returns call();
        
        /** {@inheritConstructorDoc} */
        public R0(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R1 = Return of defined type with 1 defined parameters */
    public abstract static class R1<Returns, Param1> extends ClosureConvience {
        public abstract Returns call(Param1 p1);
        
        /** {@inheritConstructorDoc} */
        public R1(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R2 = Return of defined type with 2 defined parameters */
    public abstract static class R2<Returns, Param1, Param2> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2);
        
        /** {@inheritConstructorDoc} */
        public R2(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R3 = Return of defined type with 3 defined parameters */
    public abstract static class R3<Returns, Param1, Param2, Param3> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3);
        
        /** {@inheritConstructorDoc} */
        public R3(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R4 = Return of defined type with 4 defined parameters */
    public abstract static class R4<Returns, Param1, Param2, Param3, Param4> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4);
        
        /** {@inheritConstructorDoc} */
        public R4(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R5 = Return of defined type with 5 defined parameters */
    public abstract static class R5<Returns, Param1, Param2, Param3, Param4, Param5> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5);
        
        /** {@inheritConstructorDoc} */
        public R5(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R6 = Return of defined type with 6 defined parameters */
    public abstract static class R6<Returns, Param1, Param2, Param3, Param4, Param5, Param6> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6);
        
        /** {@inheritConstructorDoc} */
        public R6(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R7 = Return of defined type with 7 defined parameters */
    public abstract static class R7<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7);
        
        /** {@inheritConstructorDoc} */
        public R7(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R8 = Return of defined type with 8 defined parameters */
    public abstract static class R8<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8);
        
        /** {@inheritConstructorDoc} */
        public R8(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R9 = Return of defined type with 9 defined parameters */
    public abstract static class R9<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9);
        
        /** {@inheritConstructorDoc} */
        public R9(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R10 = Return of defined type with 10 defined parameters */
    public abstract static class R10<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10);
        
        /** {@inheritConstructorDoc} */
        public R10(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R11 = Return of defined type with 11 defined parameters */
    public abstract static class R11<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11);
        
        /** {@inheritConstructorDoc} */
        public R11(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R12 = Return of defined type with 12 defined parameters */
    public abstract static class R12<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12);
        
        /** {@inheritConstructorDoc} */
        public R12(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R13 = Return of defined type with 13 defined parameters */
    public abstract static class R13<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13);
        
        /** {@inheritConstructorDoc} */
        public R13(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R14 = Return of defined type with 14 defined parameters */
    public abstract static class R14<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14);
        
        /** {@inheritConstructorDoc} */
        public R14(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R15 = Return of defined type with 15 defined parameters */
    public abstract static class R15<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15);
        
        /** {@inheritConstructorDoc} */
        public R15(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R16 = Return of defined type with 16 defined parameters */
    public abstract static class R16<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16);
        
        /** {@inheritConstructorDoc} */
        public R16(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R17 = Return of defined type with 17 defined parameters */
    public abstract static class R17<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17);
        
        /** {@inheritConstructorDoc} */
        public R17(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R18 = Return of defined type with 18 defined parameters */
    public abstract static class R18<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18);
        
        /** {@inheritConstructorDoc} */
        public R18(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R19 = Return of defined type with 19 defined parameters */
    public abstract static class R19<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18, Param19> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18, Param19 p19);
        
        /** {@inheritConstructorDoc} */
        public R19(Object... constructorArgs) { super(constructorArgs); }
    }

    /** R20 = Return of defined type with 20 defined parameters */
    public abstract static class R20<Returns, Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18, Param19, Param20> extends ClosureConvience {
        public abstract Returns call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18, Param19 p19, Param20 p20);
        
        /** {@inheritConstructorDoc} */
        public R20(Object... constructorArgs) { super(constructorArgs); }
    }

    /** RU = Return of defined type with undefined and unlimited amount of method parameters */
    public abstract static class RU<Returns> extends ClosureConvience {
        public abstract Returns call(Object... parameters );
        
        /** {@inheritConstructorDoc} */
        public RU(Object... constructorArgs) { super(constructorArgs); }
    }

    //======================================================================================================
    // Voids are treated specially as well to cover the complete spectrum for static declaration and usage
    // Question: Should we allow
    //======================================================================================================


    /** V0 = Void with zero parameters */
    public abstract static class V0 extends ClosureConvience {
        public abstract void call();
        
        /** {@inheritConstructorDoc} */
        public V0(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V1 = Void with 1 defined parameter */
    public abstract static class V1<Param1> extends ClosureConvience {
        public abstract void call(Param1 p1);
        
        /** {@inheritConstructorDoc} */
        public V1(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V2 = Void with 2 defined parameters */
    public abstract static class V2<Param1, Param2> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2);
        
        /** {@inheritConstructorDoc} */
        public V2(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V3 = Void with 3 defined parameters */
    public abstract static class V3<Param1, Param2, Param3> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3);
        
        /** {@inheritConstructorDoc} */
        public V3(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V4 = Void with 4 defined parameters */
    public abstract static class V4<Param1, Param2, Param3, Param4> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4);
        
        /** {@inheritConstructorDoc} */
        public V4(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V5 = Void with 5 defined parameters */
    public abstract static class V5<Param1, Param2, Param3, Param4, Param5> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5);
        
        /** {@inheritConstructorDoc} */
        public V5(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V6 = Void with 6 defined parameters */
    public abstract static class V6<Param1, Param2, Param3, Param4, Param5, Param6> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6);
        
        /** {@inheritConstructorDoc} */
        public V6(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V7 = Void with 7 defined parameters */
    public abstract static class V7<Param1, Param2, Param3, Param4, Param5, Param6, Param7> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7);
        
        /** {@inheritConstructorDoc} */
        public V7(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V8 = Void with 8 defined parameters */
    public abstract static class V8<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8);
        
        /** {@inheritConstructorDoc} */
        public V8(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V9 = Void with 9 defined parameters */
    public abstract static class V9<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9);
        
        /** {@inheritConstructorDoc} */
        public V9(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V10 = Void with 10 defined parameters */
    public abstract static class V10<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10);
        
        /** {@inheritConstructorDoc} */
        public V10(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V11 = Void with 11 defined parameters */
    public abstract static class V11<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11);
        
        /** {@inheritConstructorDoc} */
        public V11(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V12 = Void with 12 defined parameters */
    public abstract static class V12<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12);
        
        /** {@inheritConstructorDoc} */
        public V12(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V13 = Void with 13 defined parameters */
    public abstract static class V13<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13);
        
        /** {@inheritConstructorDoc} */
        public V13(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V14 = Void with 14 defined parameters */
    public abstract static class V14<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14);
        
        /** {@inheritConstructorDoc} */
        public V14(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V15 = Void with 15 defined parameters */
    public abstract static class V15<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15);
        
        /** {@inheritConstructorDoc} */
        public V15(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V16 = Void with 16 defined parameters */
    public abstract static class V16<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16);
        
        /** {@inheritConstructorDoc} */
        public V16(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V17 = Void with 17 defined parameters */
    public abstract static class V17<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17);
        
        /** {@inheritConstructorDoc} */
        public V17(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V18 = Void with 18 defined parameters */
    public abstract static class V18<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18);
        
        /** {@inheritConstructorDoc} */
        public V18(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V19 = Void with 19 defined parameters */
    public abstract static class V19<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18, Param19> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18, Param19 p19);
        
        /** {@inheritConstructorDoc} */
        public V19(Object... constructorArgs) { super(constructorArgs); }
    }

    /** V20 = Void with 20 defined parameters */
    public abstract static class V20<Param1, Param2, Param3, Param4, Param5, Param6, Param7, Param8, Param9, Param10, Param11, Param12, Param13, Param14, Param15, Param16, Param17, Param18, Param19, Param20> extends ClosureConvience {
        public abstract void call(Param1 p1, Param2 p2, Param3 p3, Param4 p4, Param5 p5, Param6 p6, Param7 p7, Param8 p8, Param9 p9, Param10 p10, Param11 p11, Param12 p12, Param13 p13, Param14 p14, Param15 p15, Param16 p16, Param17 p17, Param18 p18, Param19 p19, Param20 p20);
        
        /** {@inheritConstructorDoc} */
        public V20(Object... constructorArgs) { super(constructorArgs); }
    }

    /** VU = Void with undefined and unlimited amount of method parameters */
    public abstract static class VU extends ClosureConvience {
        public abstract void call(Object... parameters);
        
        /** {@inheritConstructorDoc} */
        public VU(Object... constructorArgs) { super(constructorArgs); }
    }
    
    //*****************************************************************************************************************
    // Closure classes ENDS here
    //*****************************************************************************************************************

    private static class ClosureConvience extends ClosureBase {
        /** {@inheritConstructorDoc} */
        public ClosureConvience(Object... constructorArgs) { super(constructorArgs); }

        /**
         * Convience methods.
         * Aware of the big letter at the beginning of the method name!
         * This is a special case attempting at mimicing a break functionality from non-returning iterating methods using closures.
         *
         * This method allows for breaking out of an iterating or recursicve method that takes a closure and allowing for the return of an object.
         * Will bubble the object all the way up to a neccessary wrapper call, which are of type Break.W
         * For example, you could use this to exit an each loop where normally you need to walk over the entire collection.
         * Once your requirement is found, you might wish to return prematurely.
         *
         * Of course if the each method already handles proper usage of pre-exiting with a return value,
         * for example by listening to what the cloj.call(..) returns in between each iteration, then in such cases this will not be needed.
         * Have made it available for use within the void closures, since the core intention of this is cases where the
         * implementor of some iterating method did not intend for pre-exiting with a return value.
         *
         */
        protected final void Break(Object object) {
            throw new Break(object);
        }
        protected final void Break() {
            throw new Break();
        }
    }
    
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    /** Convience class
     ** Supports breaking out of any Closure, such as from an iteration or recursive method such as each, eachFileRecurse, and etc..
     ** A call to Break() from within the Closure call implementaion is neccessary
     **/
    public static final class Break extends RuntimeException {
        private static final long serialVersionUID = 1L;        
        private Object obj;
        
        private Break() {
            this(null);
        }
        private Break(Object p0) {
            setObject(p0);
        }        
        private void setObject(Object p0) {
            this.obj = p0;
        }
        public Object getObject() {
            return this.obj;
        }

        /** W = Wrap for Wrapper closure, intended to be used to wrap bubbling Break(...) calls */
        public static abstract class W<Returns> extends ClosureBase {
            /** Do not call this directly, use .call() instead. Intended for wrapping an implementation utilitzing Break(...) */
            protected abstract void code();
            public final Returns call() {
                try {
                    code();
                } catch(Break r) {
                    return cast( r.getObject() );
                }
                return null;
            }
        }
    }
    //*****************************************************************************************************************   
    
    
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    /** Base class for all Closure classes
     ** Used in order not to pollute the closures.Closure class with lots of methods and properties
     **/
    private static class ClosureBase {
        private Object[] constructorArgs;
        
        /** 
         * Constructor args are only intended for passing of non-final objects that are declared inside a method! 
         * Prioritize the use of final over this if final is possible since casting an Object to a String is never compiler typesafe 
         * although the compiler will allow it. 
         **/
        protected ClosureBase(Object... constructorArgs) {
            setArgs(constructorArgs);
        }
        public void setArgs(Object[] args) {
            this.constructorArgs = args;
            index_constructorArgs = 0;
        }
        protected Object[] getArgs() {
            return constructorArgs;
        }
        protected Object getArg(int index) {
            return getArgs()[index];
        }

        //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        //******************************************************************************************************
        // Constructor arguments convience methods
        //******************************************************************************************************
        //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        private int index_constructorArgs;

        /** Casts the arguments coming from the constructor to the appropriate type */
        protected <T>T castArg(int atIndex) {
            return cast( getArg(atIndex) );
        }
        /** Casts the arguments coming from the constructor at index zero */
        protected <T>T castFirst() {
            return castFrom(0);
        }
        /** Should be initiated with a castFirst(), or castFrom(atIndex) before repeated calls to castNext() in order 
         *  to reset the counter between closure invocations, ie: while( true ) cloj.call(...); */
        protected <T>T castNext() {
            return castFrom(index_constructorArgs);
        }
        protected <T>T castFrom(int atIndex) {
            index_constructorArgs = atIndex + 1;
            return castArg(atIndex);
        }        
        //******************************************************************************************************

        // ==== Convience methods for casting ====
        protected static <T> T castAt(int index, Object[] objs) {
            if ( objs.length > index )
                return cast( objs[index] );
            return null;
        }
        @SuppressWarnings("unchecked")
        protected static <T>T cast(Object obj){
            if ( obj != null )
                return (T) obj;
            return null;
        }        
    }
    
}
