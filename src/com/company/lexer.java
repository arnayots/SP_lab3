package com.company;

import java.io.*;
import java.nio.Buffer;
import java.util.HashSet;

public class lexer {

    public lexer(String filename) throws IOException {
        fname = filename;
        File file = new File(filename);
        fr = new FileReader(file);
        reader = new BufferedReader(fr);

        //directives

        File file1 = new File(directibes_file);
        FileReader fr1 = new FileReader(file1);
        BufferedReader reader1 = new BufferedReader(fr1);
        String line = reader.readLine();
        int line_n = 1;
        while (line != null) {
            line = line.trim();
            if(!line.isEmpty())
                if(line.indexOf("#") == 0)
                    directives.add(line.substring(1));
                else
                    directives.add(line);
            line = reader.readLine();
            line_n++;
        }
        fr1.close();


        //reserved word and etc input


    }

    public void analyse() throws IOException {
        String line;
        boolean cont = true;
        line_num = 0;
        while (cont){
            line = reader.readLine();
            if(state == 0){
                if(is_directive(line)){

                }

            }

            line_num++;
        }
    }


    private boolean is_directive(String line) throws IOException {
        int first_hashtag = line.indexOf("#");
        if (first_hashtag != -1){
            boolean res = true;
            String tmp = line.substring(0, first_hashtag);
            if(is_blank_string(tmp)){
                //first nonwhite symbol is #
                tmp = line.substring(first_hashtag);
                for(String el : directives){
                    if(tmp.indexOf(el) == 0)
                        return true;
                }
                throw new IOException("In line "+line_num+" of \""+fname+"\" error. Fount symbol \"#\" withot directive");
            }
        }
        return false;
    }

    boolean is_blank_string(String string) {
        return string == null || string.trim().isEmpty();
    }


    //variables

    String fname;
    FileReader fr;
    BufferedReader reader;
    int line_num = 0;

    int state = 0;

    HashSet<String> directives;
    static String directibes_file = "directives.txt";

    String out_str = "";





}
