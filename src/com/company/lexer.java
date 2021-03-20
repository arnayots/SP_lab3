package com.company;

import java.io.*;
import java.util.ArrayList;

public class lexer {

    public lexer(String filename) throws IOException {
        fname = filename;
        File file = new File(filename);
        fr = new FileReader(file);
        reader = new BufferedReader(fr);

        //directives
/*
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
*/

        //reserved word and etc input


    }

    public void analyse() throws IOException {
        String line;
        line_num = 1;
        line = reader.readLine();
        while(line != null){
            if(state == 0){
                if(is_directive_line(line)){
                    ArrayList<String> tmp1 = new ArrayList<>();
                    tmp1.add(line);
                    out_str.add(tmp1);
                    ArrayList<token> tmp2 = new ArrayList<>();
                    tmp2.add(token.directive);
                    color.add(tmp2);
                } else {
                    cur_line = new ArrayList<>();
                    cur_color = new ArrayList<>();
                    process_line(line, 0);
                }
            }
            if(state == 1){
                System.out.println("-------------------------------");
            }
            line = reader.readLine();
            line_num++;
        }
    }


    private boolean is_directive_line(String line) throws IOException {
        int first_hashtag = line.indexOf("#");
        if (first_hashtag != -1){
            String tmp = line.substring(0, first_hashtag);
            if(is_blank_string(tmp)){
                return true;
            }
        }
        return false;
    }

    private boolean is_blank_string(String string) {
        return string == null || string.trim().isEmpty();
    }

    private void process_line(String line, int from){
        process_line(line, from, line.length());
    }

    private void process_line(String line, int from, int to){
        if(from >= to)
            return;
        if(is_blank_string(line.substring(from, to))){
            cur_line.add(line.substring(from, to));
            cur_color.add(token.clear);
        } else {
            int pos = line.indexOf("*/");
            if(pos != -1){
                process_line(line, from, pos);
                cur_line.add("*/");
                cur_color.add(token.error);
                process_line(line, pos + 2, to);
            } else {
                pos = line.indexOf("//");
                if(pos != -1){
                    process_line(line, from, pos);
                    cur_line.add("*/");
                    cur_color.add(token.error);
                    process_line(line, pos + 2, to);
                }
            }
        }
    }


    //variables

    String fname;
    FileReader fr;
    BufferedReader reader;
    int line_num = 0;

    int state = 0;

    //HashSet<String> directives;
    //static String directibes_file = "directives.txt";

    ArrayList<String> cur_line;
    ArrayList<token> cur_color;


    ArrayList<ArrayList<String>> out_str;
    ArrayList<ArrayList<token>> color;

    enum token {
        clear,
        directive,
        error
    }



}
