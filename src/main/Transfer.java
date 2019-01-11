package main;

//    http://www.styb.cn/cms/ieee_754.php

public class Transfer {

    public static int[] toBin(int num,int length){
        int[] bin = new int[length];
        int a = num>=0?num:(-num-1);
        for(int i=length-1; a>0; i--){
            bin[i] = a%2;
            a/=2;
        }
        if(num<0){
            for(int i=0; i<bin.length; i++){
                bin[i] = bin[i]==1?0:1;
            }
        }
        return bin;
    }

    public static int toInt(int[] bin){
        bin = bin.clone();
        int res = 0;
        boolean positive = true;
        if(bin[0]==1){
            positive = false;
            for(int i=0; i<bin.length; i++){
                bin[i] = bin[i]==1?0:1;
            }
        }
        for(int i=0; i<bin.length; i++){
            res += (bin[i]==1?((int)Math.pow(2, bin.length-1-i)):0);
        }
        return positive?res:(-(res + 1));
    }

    public static int[][] toFloat(String num){  //精度问题
        String[] nums = num.split("\\.");
        if(nums.length==1){
            nums = new String[]{nums[0], "0"};
        }
        int[] sign;
        if(num.charAt(0)=='-'){
            sign = new int[]{1};
            nums[0] = nums[0].substring(1);
        }
        else{
            sign = new int[]{0};
        }
        StringBuilder part1 = new StringBuilder();
        StringBuilder part2 = new StringBuilder();
        int num1 = Integer.parseInt(nums[0]);
        float num2 = Float.parseFloat("0." + nums[1]);
        while(num1>0){
            part1.insert(0, num1%2);
            num1 /= 2;
        }
        while(num2!=0){
            if((part1.length()!=0 && (part1.length()+part2.length())>=24)
                    || part1.length()==0 && (part2.length()- part2.indexOf("1"))>=24)
                break;
            part2.append(Float.toString(num2*2).charAt(0));
            num2 = num2*2>=1?(num2*2-1):num2*2;
        }
        int[] e;
        int[] tail = new int[23];
        if(part1.length()>0) {
            e = toBin(part1.length() + 126, 8);
            part1.append(part2);
            for (int i = 1; i < part1.length(); i++) {
                tail[i - 1] = part1.charAt(i) - '0';
            }
        }
        else{
            int index = 0;
            for(int i=0; i<part2.length(); i++){
                index--;
                if(part2.charAt(i)=='1'){
                    break;
                }
            }
            e = toBin(index+127, 8);
            for(int i=-index; i<part2.length(); i++){
                tail[i+index] = part2.charAt(i) - '0';
            }
        }
        return new int[][]{sign, e, tail};
    }

}
