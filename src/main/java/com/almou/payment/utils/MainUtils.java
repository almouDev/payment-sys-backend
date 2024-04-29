package com.almou.payment.utils;

import java.util.Random;

public class MainUtils {
    public static long generateRandomNumber(){
        Random random=new Random();
        return random.nextLong(100000,999999);
    }
    public static long generateUniqueNumber(){
        return System.currentTimeMillis();
    }
    public static boolean isCodeExpired(Long confirmationCode){
        return System.currentTimeMillis()>confirmationCode;
    }
    public static  Long generateExpirationTime(){
        return System.currentTimeMillis()+1000*60*30;
    }
}
