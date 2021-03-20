package com.company;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;

public class lexer {

    public lexer(String filename) throws IOException {
        fname = filename;
        File file = new File(filename);
        fr = new FileReader(file);
        reader = new BufferedReader(fr);

        //white = new HashSet<>();
        az = new HashSet<>();
        az09 = new HashSet<>();
        az09_ = new HashSet<>();
        //numbers = new HashSet<>();
        operator = new LinkedHashSet<>();
        reserved = new HashSet<>();
        delim = new HashSet<>();

        /*
        white.add(011);
        white.add(012);
        white.add(013);
        white.add(014);
        white.add(015);
        white.add(040);
*/
        delim.add(";");
        delim.add(",");

        for(int i = 'a'; i <= 'z'; i++){
            az.add(i);
            az09.add(i);
            az09_.add(i);
        }
        for(int i = '0'; i <= '9'; i++){
            az09.add(i);
            az09_.add(i);
            //numbers.add(i);
        }
        az09_.add((int)'_');

        File file1 = new File("operators.txt");
        FileReader fr1 = new FileReader(file1);
        BufferedReader reader1 = new BufferedReader(fr1);
        String data = reader1.readLine();
        while(data != null){
            if(!data.trim().isEmpty())
                operator.add(data.trim());
            data = reader1.readLine();
        }

        file1 = new File("reserved.txt");
        fr1 = new FileReader(file1);
        reader1 = new BufferedReader(fr1);
        data = reader1.readLine();
        while(data != null){
            if(!data.trim().isEmpty())
                reserved.add(data.trim().toLowerCase(Locale.ROOT));
            data = reader1.readLine();
        }







    }





    private void do_step(char c){
        if(state == 0){
          //  if(white.contains(c))

        }
        if(state == 1){

        }
        if(state == 0){

        }
    }

    public void analyse() throws IOException {
        int c = fr.read();
        while (c != -1){
            text += (char)c;
            text_draft += Character.toLowerCase(c);
            c = fr.read();
        }
        len = text.length();
        token = new color[len];
        for(int i = 0; i < len; i++)
            token[i] = color.clear;

        int i = 0;
        while(i < len){
            int pos1 = text_draft.indexOf("//", i);
            if(pos1 != -1){
                int pos2 = text_draft.indexOf("\n");
                if(pos2 == -1)
                    pos2 = len;
                for(int j = pos1; j < pos2; j++)
                    token[j] = color.comment;
                text_draft = to_white(text_draft, pos1, pos2);
                i = pos2;
            } else
                i = len;
        }

        i = 0;
        while(i < len){
            int pos1 = text_draft.indexOf("/*", i);
            if(pos1 != -1){
                int pos2 = text_draft.indexOf("*/");
                if(pos2 == -1)
                    pos2 = len;
                for(int j = pos1; j < pos2; j++)
                    token[j] = color.comment;
                text_draft = to_white(text_draft, pos1, pos2);
                i = pos2;
            } else
                i = len;
        }

        i = 0;
        while(i < len){
            int pos1 = text_draft.indexOf("#", i);
            if(pos1 != -1){
                int pos_before = text_draft.substring(0, pos1).lastIndexOf("\n");
                if(pos_before == -1 || is_blank_string(text_draft.substring(pos_before, pos1))){
                    int pos2 = text_draft.indexOf("\n");
                    for(int j = pos1; j < pos2; j++)
                        token[j] = color.directive;
                    text_draft = to_white(text_draft, pos1, pos2);
                    i = pos2;
                } else
                    i = pos1 + 1;
            } else
                i = len;
        }

        i = 0;
        while(i < len){
            int tmp1 = text_draft.indexOf("'", i);
            int tmp2 = text_draft.indexOf("\"", i);
            int pos1;
            if(tmp1 != -1 && tmp2 != -1)
                pos1 = Math.min(tmp1, tmp2);
            if(tmp1 == -1 && tmp2 == -1)
                i = len;
            else {
                if(tmp1 != -1)
                    pos1 = tmp1;
                else
                    pos1 = tmp2;
                char ch = text_draft.charAt(pos1);
                int j = pos1;
                boolean not_ok = true;
                int pos2 = -1;
                while(j < len && not_ok){
                    pos2 = text_draft.indexOf(ch, j);
                    if(pos2 != -1){
                        if(text_draft.charAt(pos2 - 1) != '\\')
                            not_ok = false;
                        else
                            j = pos2;
                    } else
                        j = len;
                }
                if(j < len && pos2 != -1){
                    if(ch == '"')
                        text_draft = set_state(text_draft, pos1, pos2 + 1, color.string);
                    else
                        text_draft = set_state(text_draft, pos1, pos2 + 1, color.charline);
                    i = pos2 + 1;
                }
            }
        }

        for(String word : operator){
            i = 0;
            while(i < len){
                int pos1 = text_draft.indexOf(word, i);
                if(pos1 != -1){
                    text_draft = set_state(text_draft, pos1, pos1 + word.length(), color.operator);
                    i = pos1 + word.length();
                } else
                    i = len;
            }
        }

        for(String word : delim){
            i = 0;
            while(i < len){
                int pos1 = text_draft.indexOf(word, i);
                if(pos1 != -1){
                    text_draft = set_state(text_draft, pos1, pos1 + word.length(), color.delim);
                    i = pos1 + word.length();
                } else
                    i = len;
            }
        }

        for(String word : reserved){
            i = 0;
            while(i < len){
                int pos1 = text_draft.indexOf(word, i);
                if(pos1 != -1){
                    text_draft = set_state(text_draft, pos1, pos1 + word.length(), color.reserved);
                    i = pos1 + word.length();
                } else
                    i = len;
            }
        }

        i = 0;
        while(i < len){
            if(token[i] == color.clear && numbers.indexOf(text_draft.charAt(i)) != -1){
                int pos1 = i;
                if(pos1 != 0 && (text.charAt(pos1 -1) == '-'))
                    pos1--;
                int pos2 = text_draft.indexOf(white, i);
                if(pos2 != -1 && text_draft.charAt(pos2 - 1) == 'e')
                    pos2 = text_draft.indexOf(white, pos2);
                if(pos2 == -1)
                    pos2 = len;
                text_draft = set_state(text_draft, pos1, pos2, color.num);



            }
        }



    }

    private String set_state(String str, int pos1, int pos2, color st){
        if(pos1 >= pos2)
            return str;
        if(pos2 > str.length())
            pos2 = str.length();
        for(int j = pos1; j < pos2; j++)
            token[j] = st;
        str = to_white(str, pos1, pos2);
        return str;
    }

    private String to_white(String str, int i, int j){
        if(i >= j)
            return str;
        String tmp = "";
        for(int k = 0; k < j - i; k++)
            tmp += " ";
        return str.substring(0, i) + tmp + str.substring(j);
    }




/*
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


 */




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

    /*
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
            int pos = line.indexOf("");
            if(pos != -1){
                process_line(line, from, pos);
                cur_line.add("");
                cur_color.add(token.error);
                process_line(line, pos + 2, to);
            } else {
                pos = line.indexOf("//");
                if(pos != -1){
                    process_line(line, from, pos);
                    cur_line.add("");
                    cur_color.add(token.error);
                    process_line(line, pos + 2, to);
                }
            }
        }
    }

*/

    //variables

    String fname;
    FileReader fr;
    BufferedReader reader;
    int line_num = 0;

    int state = 0;

    //0 - start of line
    //1 - white char

    //HashSet<String> directives;
    //static String directibes_file = "directives.txt";

    /*
    ArrayList<String> cur_line;
    ArrayList<token> cur_color;


    ArrayList<ArrayList<String>> out_str;
    ArrayList<ArrayList<token>> color;
    */

    enum color {
        clear,
        white,
        comment,
        directive,
        string,
        charline,
        operator,
        delim,
        reserved,
        num,
        error
    }

    //HashSet<Integer> white;
    String white = "\011\012\013\014\015\040";
    HashSet<Integer> az;
    HashSet<Integer> az09;
    HashSet<Integer> az09_;
    //HashSet<Integer> numbers;
    String numbers = "0123456789";
    LinkedHashSet<String> operator;
    HashSet<String> reserved;
    HashSet<String> delim;

    String text = "";
    String text_draft = "";
    color[] token;
    int len;




}
