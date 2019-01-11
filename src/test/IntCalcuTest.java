package test;

import main.FloCalcu;
import main.IntCalcu;
import main.Transfer;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;

public class IntCalcuTest {

    Random ra = new Random();

    @Test
    public void testAdd(){
//        int num1 = ra.nextInt();
//        int num2 = ra.nextInt();
        int num1 = -100;
        int num2 = -6;
        assertEquals(num1 + num2,
                Transfer.toInt(
                        IntCalcu.Add(Transfer.toBin(num1, 32),
                                Transfer.toBin(num2, 32))));
    }

    @Test
    public void testSub(){
//        int num1 = ra.nextInt();
//        int num2 = ra.nextInt();
//        int num1 = 0;
//        int num2 = -6;
//        assertEquals(num1 - num2,
//                Transfer.toInt(
//                        IntCalcu.Sub(Transfer.toBin(num1, 32),
//                                Transfer.toBin(num2, 32))));
        assertEquals(new int[]{},
                IntCalcu.Sub(new int[]{0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        new int[]{0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}));
    }

    @Test
    public void testMult(){
        int num1 = -3;
        int num2 = -2;
        assertEquals(num1 * num2,
                Transfer.toInt(
                        IntCalcu.Mult(new int[]{0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                                new int[]{0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0})));
    }

    @Test
    public void testRDivi(){   //被除数为负，且能整除
//        int num1 = ra.nextInt();
//        int num2 = ra.nextInt();
        int num1 = 6;
        int num2 = -2;
        assertEquals(new int[][]{Transfer.toBin(num1/num2, 32), Transfer.toBin(num1%num2, 32)},
                IntCalcu.RDivi(Transfer.toBin(num1, 32), Transfer.toBin(num2,32)));
    }

    @Test
    public void testDivi(){      //被除数为负，且能整除
//        int num1 = ra.nextInt();
//        int num2 = ra.nextInt();
        int num1 = 6;
        int num2 = -2;
        assertEquals(new int[][]{Transfer.toBin(num1/num2, 32), Transfer.toBin(num1%num2, 32)},
                IntCalcu.Divi(Transfer.toBin(num1, 32), Transfer.toBin(num2,32)));
    }

    @Test
    public void testToFloat(){
        assertEquals(new int[][]{{0},{0,1,1,1,1,0,0,1},{0,1,1,1,1,0,0,0,1,1,0,1,0,1,0,0,1,1,1,0,0,0,0}}, Transfer.toFloat("0.023"));
    }

    @Test
    public void testFAdd(){
        assertEquals(new int[][]{{0},{0,1,1,1,1,0,1,1},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}},
                FloCalcu.Add(Transfer.toFloat("0.5"), Transfer.toFloat("-0.4375")));
    }

    @Test
    public void testFSub(){
        assertEquals(new int[][]{{1}, {0,1,1,1,1,0,1,1}, {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}},
                FloCalcu.Sub(Transfer.toFloat("-0.5"), Transfer.toFloat("-0.4375")));
    }

    @Test
    public void testFMult(){
        assertEquals(new int[][]{{1}, {1,0,0,0,0,1,1,1},{1,1,0,0,1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0}},
                FloCalcu.Mult(Transfer.toFloat("23.5"), Transfer.toFloat("-19.5")));
    }

    @Test
    public void testFDivi(){
        assertEquals(new int[][]{{0}, {0,1,1,1,1,1,1,0},{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}},
                FloCalcu.Divi(Transfer.toFloat("0.4375"), Transfer.toFloat("0.5")));
    }

    @Test
    public void testDecimalAdd(){
        assertEquals(Arrays.toString(new int[]{1,0,0,0,0,0,0,0,1,0,1,0,0}),
                Arrays.toString(IntCalcu.DecimalAdd(new int[]{0,0,0,1,0,0,1,0,1},
                        new int[]{1,0,0,1,1,1,0,0,1})));
    }

    @Test
    public void testDecimalSub(){
        assertEquals(Arrays.toString(new int[]{1,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0}),
                Arrays.toString(IntCalcu.DecimalSub(new int[]{0,0,0,0,1,0,0,1,0,0,1,0,1},
                        new int[]{0,0,0,0,1,0,1,1,1,0,1,0,1})));
    }

    @Test
    public void testStrAdd(){
        int num1 = -10;
        int num2 = 20;
        String res = IntCalcu.strToBin(num1+num2);
        assertEquals(res, IntCalcu.strAdd(num1, num2));
    }
}