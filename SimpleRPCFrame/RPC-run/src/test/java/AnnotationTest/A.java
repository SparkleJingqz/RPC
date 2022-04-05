package AnnotationTest;

import java.lang.annotation.Annotation;


@Test1
public class A {

    public int a = 0;

    public static void main(String[] args) {
        System.out.println(B.class.isAnnotationPresent(Test1.class));
    }



}

class B extends A {

}
