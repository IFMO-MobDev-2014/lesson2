package ru.ifmo.md.lesson2;

/**
 * @author volhovm
 *         Created on 9/21/14
 */

public class ColorTest {
    public static void main(String[] args) {
        System.out.println(0x44AADE33);
        System.out.println((0x44AADE33 & 0x00FF0000) / 0x0000FF00);
//        System.out.println(Color.red());
    }
}
