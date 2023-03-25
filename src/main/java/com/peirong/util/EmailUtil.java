package com.peirong.util;


import lombok.Data;

import java.math.BigInteger;
import java.util.Random;

@Data
public class EmailUtil {

    private String code;

    public void emailUtil(String code) {
        this.code = code;
    }

    public void setCode(String code) {
        //Random random = new Random();

        //Integer i = Math.random(1000,9999);

        //System.out.println(i);
        //code = i.toString();
    }

    public String getCode() {
        return code;
    }
}
