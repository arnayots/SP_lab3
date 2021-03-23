package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try{
            //lexer a = new lexer("D:\\DATA\\Program Lab&HW\\Lab 3\\6\\Code\\Record.cpp");
            lexer a = new lexer("code2.txt");
            //lexer a = new lexer("D:\DATA\Program 2 Labs\Lab 3\\alg.cpp");
            //lexer a = new lexer("D:\\DATA\\Program 2 Labs\\Modul1\\Stoian.cpp");

            a.analyse();
            //a.create_html("index2.html");
            a.create_html("index3.html");
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
