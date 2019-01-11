package main;

public class FloCalcu {

    /*
    加法：
        1.如果有零，则结果为另一数
        2.对齐阶值，将对齐后的尾数（24位）存入暂存器
        3.尾数有符号相加->若是加法，则直接相加;若是减法，则改变第二个数的符号
            a).若两操作数符号相同，则直接相加
                若最高位进位，尾数右移一次(最前面补1)，阶值+1，结果符号即操作数符号
            b).若不同，则将第二个操作数的尾数取反+1，且将其符号位改变，再相加
                i).如果最高位进位，结果符号与第二个操作数相同
                ii).没有进位，将尾数取反+1,符号与第二个操作数相反
        4.尾数规格化
     */
    public static int[][] Add(int[][] num1, int[][] num2){
        num1 = num1.clone();
        num2 = num2.clone();
        if(isZero(num1)||isZero(num2))
            return isZero(num1)?num2:num1;
        else{
            int[][] res = new int[][]{new int [num1[0].length], new int[num1[1].length], new int[num1[2].length]};
            int[] temp1 = new int[32];
            int[] temp2 = new int[32];
            int e1 = Transfer.toInt(num1[1]);
            int e2 = Transfer.toInt(num2[1]);
            res[1] = e1>e2?num1[1].clone():num2[1].clone();
            int offset1 = e1>=e2?0:(e2-e1);
            int offset2 = e2>=e1?0:(e1-e2);
            temp1[offset1] = 1;
            for(int i=offset1+1; i<num1[2].length+offset1+1 && i<temp1.length; i++){
                temp1[i] = num1[2][i-offset1-1];
            }
            temp2[offset2] = 1;
            for(int i=offset2+1; i<num2[2].length+offset2+1 && i<temp2.length; i++){
                temp2[i] = num2[2][i-offset2-1];
            }
            int[] temp = new int[temp1.length];
            int carry = 0;
            if(num1[0][0]==num2[0][0]){
                res[0][0] = num1[0][0];
                for(int i=temp.length-1; i>=0; i--){
                    temp[i] = temp1[i]^temp2[i]^carry;
                    carry = temp1[i]&temp2[i] | temp1[i]&carry | temp2[i]&carry;
                }
                if(carry==1){
                    res[1] = IntCalcu.Add(res[1], Transfer.toBin(1, res[1].length));
                    for(int j=temp.length-1; j>0; j--){
                        temp[j] = temp[j-1];
                    }
                    temp[0] = 1;
                }
            }
            else{
                num2[0][0] = num2[0][0]==1?0:1;
                temp2 = Transfer.toBin(-Transfer.toInt(temp2), temp2.length);
                for(int i=temp.length-1; i>=0; i--){
                    temp[i] = temp1[i]^temp2[i]^carry;
                    carry = temp1[i]&temp2[i] | temp1[i]&carry | temp2[i]&carry;
                }
                if(carry==0){
                    res[0][0] = num2[0][0]==0?1:0;
                    temp = Transfer.toBin(-Transfer.toInt(temp), temp.length);
                }
                else{
                    res[0][0] = num2[0][0];
                    if(IntCalcu.isZero(temp)){
                        res[1] = Transfer.toBin(0, res[1].length);
                        return res;
                    }
                }
            }
            //尾数规格化
            int dist;
            for(dist=0; dist<temp.length; dist++){
                if(temp[dist]==1)
                    break;
            }
            res[1] = IntCalcu.Sub(res[1], Transfer.toBin(dist, res[1].length));
            for(int i=dist+1; i<temp.length && i<num1[2].length+dist+1; i++){
                res[2][i-dist-1] = temp[i];
            }
            return res;
        }
    }

    /*
    减法：
    改变第二个操作数的符号，执行加法
     */
    public static int[][] Sub(int[][] num1, int[][] num2){
        num2 = num2.clone();
        num2[0][0] = num2[0][0]==1?0:1;
        return Add(num1, num2);
    }

    /*
    乘法：
        1.若有零，则结果为0
        2.阶值=阶值1+阶值2-127 (无符号数加减法)
        3.符号=符号1异或符号2
        4.尾数（24位）相乘
            无符号数booth算法：
                1.扩展n位0存入暂存器1，乘数1存入暂存器2
                2.判断Temp2（0）
                    a).若是0，则直接右移
                    b).若是1，则暂存器1+乘数2，再右移
                3.结果为48位，尾数为47-24位
        5.规格化
            a).若尾数最高位是0，则左移一位
            b).若最高位是1，则尾数不变，阶值+1
        6.舍入
     */
    public static int[][] Mult(int[][] num1, int[][] num2){
        num1 = num1.clone();
        num2 = num2.clone();
        int[][] res = new int[][]{new int[num1[0].length], new int[num1[1].length], new int[num1[2].length]};
        if(isZero(num1)||isZero(num2))
            return res;
        else {
            res[0][0] = num1[0][0] ^ num2[0][0];
            res[1] = IntCalcu.Sub(IntCalcu.Add(num1[1], num2[1]), Transfer.toBin(127, num1[1].length));
            int[] temp1 = new int[num1[2].length+1];
            temp1[0] = 1;
            for(int i=1; i<temp1.length; i++){
                temp1[i] = num1[2][i-1];
            }
            int[] temp2 = new int[num1[2].length+1];
            temp2[0] = 1;
            for(int i=1; i<temp2.length; i++){
                temp2[i] = num2[2][i-1];
            }
            int[] temp = NonSignMult(temp1, temp2);
            if(temp[0]==0){
                for(int i=0; i<temp.length-1; i++){
                    temp[i] = temp[i+1];
                }
                temp[temp.length-1] = 0;
            }
            else{
                res[1] = IntCalcu.Add(res[1], Transfer.toBin(1, res[1].length));
            }
            for(int i=0; i<res[2].length; i++){
                res[2][i] = temp[i+1];
            }
            return res;
        }
    }

    /*
    除法：
        1.符号为操作数1异或操作数2
        2.阶值为操作数1-操作数2+127 （无符号数加减法）
        3.尾数相除(无符号除法，会用到无符号减法)
        4.规格化
     */
    public static int[][] Divi(int[][] num1, int[][] num2){
        num1 = num1.clone();
        num2 = num2.clone();
        int[][] res = new int[][]{new int[num1[0].length], new int[num1[1].length], new int[num1[2].length]};
        res[0][0] = num1[0][0]^num2[0][0];
        res[1] = IntCalcu.Add(IntCalcu.Sub(num1[1], num2[1]), Transfer.toBin(127, num1[1].length));
        int[] temp1 = new int[num1[2].length+1];
        temp1[0] = 1;
        for(int i=1; i< temp1.length; i++){
            temp1[i] = num1[2][i-1];
        }
        int[] temp2 = new int[num2[2].length+1];
        temp2[0] = 1;
        for(int i=1; i<temp2.length; i++){
            temp2[i] = num2[2][i-1];
        }
        int[] temp = NonSignDivi(temp1, temp2);
        //尾数规格化
        int dist;
        for(dist=0; dist<temp.length; dist++){
            if(temp[dist]==1)
                break;
        }
        res[1] = IntCalcu.Sub(res[1], Transfer.toBin(dist, res[1].length));
        for(int i=dist+1; i<temp.length && i<num1[2].length+dist+1; i++){
            res[2][i-dist-1] = temp[i];
        }
        return res;
    }

    /*
        1.将被除数放入暂存器1，新建暂存器2（商）
        2.判断余数够不够减
            a).若够减，则相减放入暂存器1，商设为1
            b).不够减，则不减，商设为0
        3.左移
        4.重复步骤2,3
        商在寄存器2中，余数在寄存器1中
     */
    public static int[] NonSignDivi(int[] bin1, int[] bin2){
        bin1 = bin1.clone();
        bin2 = bin2.clone();
        int[] res = new int[bin1.length];
        int Qn;
        int[] temp1 = new int[bin1.length+1];
        int[] temp2 = new int[bin2.length+1];
        int[] temp;
        for(int i=0; i<bin1.length; i++){
            temp1[0] = 0;
            for(int j=1 ;j<temp1.length; j++){
                temp1[j] = bin1[j-1];
            }
            temp2[0] = 0;
            for(int j=1; j<temp2.length; j++){
                temp2[j] = bin2[j-1];
            }
            temp = IntCalcu.Sub(temp1, temp2);
            if(temp[0]==0 && Transfer.toInt(temp)>=0){
                for(int k=0; k<bin1.length; k++){
                    bin1[k] = temp[k+1];
                }
                Qn = 1;
            }
            else{
                Qn = 0;
            }
            for(int j=0; j<bin1.length-1; j++){
                bin1[j] = bin1[j+1];
            }
            bin1[bin1.length-1] = res[0];
            for(int j=0; j<res.length-1; j++){
                res[j] = res[j+1];
            }
            res[res.length-1] = Qn;
        }
        return res;
    }


    public static int[] NonSignMult(int[] bin1, int[] bin2){
        bin1 = bin1.clone();
        bin2 = bin2.clone();
        int[] temp = new int[bin1.length];
        for(int i=0; i<bin1.length; i++){
            if(bin1[bin1.length-1]==1){
                temp = NonSignAdd(temp, bin2);
            }
            for(int j=bin1.length-1; j>0; j--){
                bin1[j] = bin1[j-1];
            }
            bin1[0] = temp[temp.length-1];
            for(int k=temp.length-1; k>0; k--){
                temp[k] = temp[k-1];
            }
            temp[0] = 0;
        }
        int[] res = new int[temp.length*2];
        for(int i=0; i<temp.length; i++){
            res[i] = temp[i];
        }
        for(int i=temp.length; i<res.length; i++){
            res[i] = bin1[i-temp.length];
        }
        return res;
    }

    public static int[] NonSignAdd(int[] bin1, int[] bin2){
        bin1 = bin1.clone();
        bin2 = bin2.clone();
        int[] res = new int[bin1.length];
        int carry = 0;
        for(int i=res.length-1; i>=0; i--){
            res[i] = bin1[i]^bin2[i]^carry;
            carry = bin1[i]&carry | bin2[i]&carry | bin1[i]&bin2[i];
        }
        return res;
    }

    public static boolean isZero(int[][] num1){
        return Transfer.toInt(num1[1])==0 && Transfer.toInt(num1[2])==0;
    }

}
