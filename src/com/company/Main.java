package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try{
            lexer a = new lexer("D:\\DATA\\Program Lab&HW\\Lab 3\\6\\Code\\lexer.cpp");
            a.analyse();
            a.create_html("index2.html");
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
