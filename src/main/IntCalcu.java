package main;

import java.util.Arrays;

public class IntCalcu {

    public static void main(String[] args) {
        int num = 100;
//        System.out.println(num);
//        StringBuilder str = new StringBuilder();
//        str.append(Integer.toBinaryString(num));
//        while(str.length()<32)
//            str.insert(0, '0');
//        System.out.println(str);
        StringBuilder a = new StringBuilder();
        a.append("01234");
        a.insert(0, "A");
        System.out.println(a);
    }

    public static boolean isZero(int[] num){
        return Transfer.toInt(num)==0;
    }

    /*
    整数加法运算加法：
        若有零，则结果即为另一操作数或相反数
        溢出：两操作数符号相同时，结果符号改变
     */
    public static int[] Add(int[] num1, int[] num2){
        if(isZero(num1)||isZero(num2)){
            return isZero(num1)?num2.clone():num1.clone();
        }
        else{
            int carry = 0;
            int[] num = new int[num1.length];
            for(int i=num.length-1; i>=0; i--){
                num[i] = num1[i]^num2[i]^carry;
                carry = num1[i]&num2[i] | num1[i]&carry | num2[i]&carry;
            }
            return num;
        }
    }

    /*
    减法：对减数取相反数，然后做加法
     */
    public static int[] Sub(int[] num1, int[] num2){
        num2 = Transfer.toBin(-Transfer.toInt(num2), num2.length);
        return Add(num1, num2);
    }

    /*
    乘法：操作数同为32位，结果为64位
        0.将乘数1放入结果2，结果1为空
        1.若有数为0，则结果为0
        2.在乘数2末尾添一位Q(-1)0
            a).判断Q(-1)-Q(0)的结果
                若是-1，则结果-乘数
                若是1，则结果+乘数
                若是0，则不管
            b).结果算数右移
        3.重复步骤2，执行次数为乘数2的位数
        结果在结果1+结果2中
     */
    public static int[] Mult(int[] num1, int[] num2){ //something wrong
        int[] res = new int[num1.length * 2];
        if(isZero(num1)||isZero(num2))
            return res;
        else {
            int support = 0;
            int[] res1 = new int[num1.length];
            for(int i=0; i<num2.length; i++) {
                switch (support - num1[num1.length - 1]) {
                    case 1:
                        res1 = Add(res1, num2);
                        break;
                    case -1:
                        res1 = Sub(res1, num2);
                        break;
                }
                support = num1[num1.length-1];
                for(int j=num1.length-1; j>0; j--){
                    num1[j] = num1[j-1];
                }
                num1[0] = res1[res1.length-1];
                for(int j=res1.length-1; j>0; j--){
                    res1[j] = res1[j-1];
                }
                res1[0] = res1[1];
            }
            for(int i=0; i<res1.length; i++){
                res[i] = res1[i];
            }
            for(int i=res1.length; i<res.length; i++){
                res[i] = num1[i-res1.length];
            }
            return res;
        }
    }

    /*
    恢复余数除法：
        1.被除数符号位扩展，存入余数寄存器，被除数存入商寄存器
        2.将余数和商左移
        3.余数与除数符号相同则相减，符号不同则相加
        4.判断新余数的符号
            a).若符号改变，则恢复成原来的余数，商设为0
            b).若符号不改变，商设为1
        重复步骤2-4，总执行次数为寄存器的位数
        5.如果余数和除数绝对值相同，则余数为0，商+1   ？？？
        结果：余数->余数寄存器
              商-> a).若除数与被除数符号不同，真正的商为所求结果的相反数
    */
    public static int[][] RDivi(int[] num1, int[] num2){
        num1 = num1.clone();
        num2 = num2.clone();
        int[] remainder = new int[num1.length];
        boolean sameSign = num1[0]==num2[0];
        if(num1[0]==1)
            Arrays.fill(remainder, 1);
        int[] temp;
        for(int i=0; i<num2.length; i++){
            for(int k=0; k<remainder.length-1; k++){
                remainder[k] = remainder[k+1];
            }
            remainder[remainder.length-1] = num1[0];
            for(int k=0; k<num1.length-1; k++){
                num1[k] = num1[k+1];
            }
            temp = remainder[0]==num2[0]?Sub(remainder, num2):Add(remainder, num2);
            if(remainder[0] == temp[0]){
                remainder = temp;
                num1[num1.length-1] = 1;
            }
            else{
                num1[num1.length-1] = 0;
            }
        }
        if(Math.abs(Transfer.toInt(remainder))==Math.abs(Transfer.toInt(num2))){
            remainder = new int[32];
            num1 = Add(num1, Transfer.toBin(1, num1.length));
        }
        num1 = sameSign?num1:Transfer.toBin(-(Transfer.toInt(num1.clone())), num1.length);
        return new int[][]{num1, remainder};
    }

    /*
    不恢复余数除法：
        1.将被除数符号位扩展存入余数寄存器，被除数存入商寄存器
        2.若被除数与除数符号相同，做减法；若不同，做加法
            a).若余数与除数符号相同，辅助位Qn=1
            b).若不同，辅助位Qn=0
        3.若余数与除数有相同的符号，余数左移-除数；符号不同，余数左移+余数
            a).若新余数与除数符号相同，商设为1
            b).符号不同，商设为0
        4.重复步骤3，总次数为除数的位数
        5.将商左移
            a).若被除数和除数符号不同，则商+1
            b).若余数和被除数符号不同
                i).若除数和被除数符号相同，则余数+除数
                ii).若不同，余数-除数
     */
    public static int[][] Divi(int[] num1, int[]num2){
        num1 = num1.clone();
        num2 = num2.clone();
        int[] remainder = new int[num1.length];
        int Qn;
        int dividendSign = num1[0];
        if(num1[0]==1)
            Arrays.fill(remainder, 1);
        remainder = num1[0]==num2[0]?Sub(remainder, num2):Add(remainder, num2);
        Qn = remainder[0]==num2[0]?1:0;
        for(int i=0; i<num2.length; i++){
            boolean sign = remainder[0] == num2[0];
            for(int k=0; k<remainder.length-1;k++){
                remainder[k] = remainder[k+1];
            }
            remainder[remainder.length-1] = num1[0];
            for(int k=0; k<num1.length-1; k++){
                num1[k] = num1[k+1];
            }
            num1[num1.length-1] = Qn;
            remainder = sign?Sub(remainder, num2):Add(remainder, num2);
            Qn = remainder[0]==num2[0]?1:0;
        }
        for(int i=0; i<num1.length-1; i++){
            num1[i] = num1[i+1];
        }
        num1[num1.length-1] = Qn;
        num1 = !(dividendSign == num2[0]) ? Add(num1, Transfer.toBin(1, num1.length)) : num1;
        if (remainder[0] != dividendSign) {
            remainder = dividendSign == num2[0] ? Add(remainder, num2) : Sub(remainder, num2);
        }
        return new int[][]{num1, remainder};
    }

        /*
    8421码 -> 取相反数：每一位（4位表示）取反，加1010（10），个位再加1->可将初始carry+1
        1.将十进制数转换为4位二进制数
            a).如果是正数，则直接将每一位转换为4位二进制
            b).如果是负数，在转换后还需取相反数
        2.
            a).如果是减法，则将减数取相反数,若最后结果最高位有进位，则为整数；否则，为负数（结果取相反数）
            b).如果是加法，就不管
        3.根据十进制运算规则，从个位开始逐位运算
            a).若大于15，则产生进位，则需将carry设为1
            b).若大于10且小于16，也产生进位，除了将carry设为1外，该位还需加0110（6）
        4.完成上述步骤后，若carry为1
            a).若两操作数同号，则表明结果位数+1
            b).若两操作数异号，则忽略此进位
     */
    public static int[] DecimalAdd(int[] num1, int[] num2){
        num1 = num1.clone();
        num2 = num2.clone();
        int[] res = new int[num1.length+4];
        boolean sameSign = num1[0]==num2[0];
        int carry = 0;
        if(!sameSign)
            num2 = Oppose(num2);
        int[] digit = new int[4];
        int[] six = new int[]{0,1,1,0};
        int car;
        int[] temp;
        for(int i=num2.length-1; i>=1; i-=4){
            for(int k=i; k>=i-3; k--){
                res[k+4] = num1[k]^num2[k]^carry;
                carry = num1[k]&carry | num2[k]&carry | num1[k]&num2[k];
            }
            for(int j=i; j>=i-3; j--){
                digit[j-i+3] = res[j+4];
            }
            if(ToInt(digit)>=10 || carry==1){
                carry = 1;
                car = 0;
                temp = res.clone();
                for(int p=i; p>=i-3; p--){
                    res[p+4] = temp[p+4]^six[p-i+3]^car;
                    car = temp[p+4]&car | six[p-i+3]&car | six[p-i+3]&temp[p+4];
                }
            }
        }
        if(sameSign){
            res[0] = num1[0];
            if(carry==1){
                res[4] = 1;
            }
        }
        else{
            if(carry==1){
                res[0] = 0;
            }
            else{
                res = Oppose(res);
                for(int i=1; i<=4; i++){
                    res[i] = 0;
                }
                res[0] = 1;
            }
        }
        return res;
    } // right?

    public static int[] DecimalSub(int[] num1, int[] num2){
        num1 = num1.clone();
        num2 = num2.clone();
        num2[0] = num2[0]==0?1:0;
        return DecimalAdd(num1, num2);
    }


    /*
    8421码取相反数
     */
    private static int[] Oppose(int[] num){
        num = num.clone();
        int[] temp;
        int carry;
        for(int i=num.length-1; i>=1; i-=4){
            for(int k=i; k>=i-3; k--){
                num[k] = num[k]==0?1:0;
            }
            carry = 1;
            int[] ten = new int[]{1,0,1,0};
            temp = num.clone();
            for(int k=i; k>=i-3; k--){
                num[k] = temp[k]^ten[k-i+3]^carry;
                carry = temp[k]&carry | temp[k]&ten[k-i+3] | ten[k-i+3]&carry;
            }
        }
//        temp = num.clone();
//        carry = 1;
//        for(int i=num.length-1; i>=num.length-4; i--){
//            num[i] = temp[i]^carry;
//            carry = temp[i]&carry;
//        }
        return num;
    }

    /*
    无符号数转int
     */
    private static int ToInt(int[] num){
        int res = 0;
        for(int i=num.length-1; i>=0; i--){
            res = num[i]==1?(res + (int)Math.pow(2, num.length-i-1)):res;
        }
        return res;
    }

    public static String strAdd(int num1, int num2){
        String str1 = strToBin(num1);
        String str2 = strToBin(num2);
        StringBuilder res = new StringBuilder();
        char carry = '0';
        for(int i=str1.length()-1; i>=0; i--){
            char c1 = str1.charAt(i);
            char c2 = str2.charAt(i);
            res.insert(0, c1^c2^carry - '0');
            carry = (char)(c1&carry | c2&carry | c1&c2);
        }
        return res.toString();
    }

    public static String strToBin(int num){
        StringBuilder str = new StringBuilder();
        str.append(Integer.toBinaryString(num));
        while(str.length()<32){
            str.insert(0,'0');
        }
        return str.toString();
    }

}
