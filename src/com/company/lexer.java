package com.company;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;

import static com.company.lexer.color.*;

public class lexer {

    public lexer(String filename) throws IOException {
        fname = filename;
        File file = new File(filename);
        fr = new FileReader(file);
        reader = new BufferedReader(fr);

        operator = new LinkedHashSet<>();
        reserved = new HashSet<>();
        delim = new HashSet<>();

        delim.add(";");
        delim.add(",");
        delim.add("{");
        delim.add("}");

        for(int i = 'a'; i <= 'z'; i++){
            az += (char)i;
            az09 += (char)i;
            az09_ += (char)i;
        }
        for(int i = '0'; i <= '9'; i++){
            az09 += (char)i;
            az09_ += (char)i;
            numeric += (char)i;
            //numbers.add(i);
        }
        az09_ += "_";
        numeric += "-";

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

    public void analyse() throws IOException {
        int c = fr.read();
        while (c != -1){
            text += (char)c;
            text_draft += String.valueOf((char)Character.toLowerCase(c));
            c = fr.read();
        }
        len = text.length();
        token = new color[len];
        for(int i = 0; i < len; i++)
            token[i] = clear;

        //comment 1
        int i = 0;
        while(i < len){
            int pos1 = text_draft.indexOf("//", i);
            if(pos1 != -1){
                int pos2 = text_draft.indexOf("\n", pos1);
                if(pos2 == -1)
                    pos2 = len;
                set_state(text_draft, pos1, pos2, comment);
                i = pos2;
            } else
                i = len;
        }

        //comment 2
        i = 0;
        while(i < len){
            int pos1 = text_draft.indexOf("/*", i);
            if(pos1 != -1){
                int pos2 = text_draft.indexOf("*/", pos1) + 2;
                if(pos2 == -1)
                    pos2 = len;
                set_state(text_draft, pos1, pos2, comment);
                i = pos2;
            } else
                i = len;
        }

        //directives
        i = 0;
        while(i < len){
            int pos1 = text_draft.indexOf("#", i);
            if(pos1 != -1){
                int pos_before = text_draft.substring(0, pos1).lastIndexOf("\n");
                if(pos_before == -1 || is_blank_string(text_draft.substring(pos_before, pos1))){
                    int pos2 = text_draft.indexOf("\n", pos1);
                    if(pos2 == -1)
                        pos2 = len;
                    set_state(text_draft, pos1, pos2, directive);
                    i = pos2;
                } else
                    i = pos1 + 1;
            } else
                i = len;
        }

        //string and c-strings
        i = 0;
        while(i < len){
            int tmp1 = text_draft.indexOf("'", i);
            int tmp2 = text_draft.indexOf("\"", i);
            int pos1 = -1;
            if(tmp1 != -1 && tmp2 != -1)
                pos1 = Math.min(tmp1, tmp2);
            else{
                if(tmp1 == -1 && tmp2 == -1)
                    i = len;
                else {
                    if(tmp1 != -1)
                        pos1 = tmp1;
                    else
                        pos1 = tmp2;
                }
            }
            if(pos1 != -1) {
                char ch = text_draft.charAt(pos1);
                int j = pos1 + 1;
                boolean not_ok = true;
                int pos2 = -1;
                while (j < len && not_ok) {
                    pos2 = text_draft.indexOf(ch, j);
                    if (pos2 != -1) {
                        if (text_draft.charAt(pos2 - 1) != '\\')
                            not_ok = false;
                        else
                            j = pos2;
                    } else
                        j = len;
                }
                if (j < len && pos2 != -1) {
                    if (ch == '"')
                        text_draft = set_state(text_draft, pos1, pos2 + 1, string);
                    else
                        text_draft = set_state(text_draft, pos1, pos2 + 1, charline);
                    i = pos2 + 1;
                }
            }
        }

        //operators
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

        //delims
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

        //reserved
        for(String word : reserved){
            i = 0;
            while(i < len){
                int pos1 = text_draft.indexOf(word, i);
                if(pos1 != -1){
                    int tmp1 = pos1 - 1, tmp2 = pos1 + word.length() + 1;
                    if(pos1 == 0)
                        tmp1++;
                    if(tmp2 == len)
                        tmp2--;
                    String tmp = text_draft.substring(tmp1, tmp2).trim();
                    if(tmp.equals(word))
                        text_draft = set_state(text_draft, pos1, pos1 + word.length(), color.reserved);
                    i = pos1 + word.length();
                } else
                    i = len;
            }
        }

        //identificators
        i = 0;
        while(i < len){
            if(az.indexOf(text_draft.charAt(i)) != -1 && token[i] == color.clear){
                if(i == 0 || white.indexOf(String.valueOf((char)text_draft.charAt(i - 1))) != -1){
                    int j = i + 1;
                    while(j < len && token[i] == color.clear && az09_.indexOf(text_draft.charAt(j)) != -1)
                        j++;
                    int pos2 = j;
                    if(j == len)
                        pos2 = len;
                    text_draft = set_state(text_draft, i, pos2, color.ident);
                    i = pos2;
                } else
                    i++;
            } else
                i++;
        }

        //numbers
        i = 0;
        while(i < len){
            if(numeric.indexOf(text_draft.charAt(i)) != -1 && token[i] == color.clear){
                if(i == 0 || white.indexOf(String.valueOf((char)text_draft.charAt(i - 1))) != -1){
                    int j = i + 1;
                    while(j < len && token[i] == color.clear && white.indexOf(text_draft.charAt(j)) == -1)
                        j++;
                    int pos2 = j;
                    //if(j == len)
                    //    pos2 = len;
                    text_draft = set_state(text_draft, i, pos2, color.num);
                    i = pos2;
                } else
                    i++;
            } else
                i++;
        }

        //whites
        for(i = 0; i < len; i++){
            if(token[i] == clear && white.indexOf(text_draft.charAt(i)) != -1)
                token[i] = color.white;
        }

        //errors
        for(i = 0; i < len; i++){
            if(token[i] == clear)
                token[i] = color.error;
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

    private boolean is_blank_string(String string) {
        return string == null || string.trim().isEmpty();
    }

    public void create_html(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>");
        writer.write(fname);
        writer.write("</title>\n" +
                "<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n" +
                "<link href=\"https://fonts.googleapis.com/css2?family=Source+Code+Pro:wght@400;500&display=swap\" rel=\"stylesheet\">\n" +
                "    <style>\n" +
                "   body {\n" +
                "    font-family: \"Source Code Pro\", monospace;\n" +
                "    white-space: pre;\n" +
                "    font-weight: 500\n" +
                "   }\n" +
                "   p {\n" +
                "    margin : 0\n" +
                "   }\n" +
                "   .clear {\n" +
                "    background-color: #dff;\n" +
                "    color: #500;\n" +
                "   }\n" +
                "   .white {\n" +
                "    background-color: #fff;\n" +
                "    color: #050;\n" +
                "   }\n" +
                "   .comment {\n" +
                "    background-color: #efe;\n" +
                "    color: #999;\n" +
                "   }\n" +
                "   .directive {\n" +
                "    background-color: #fee;\n" +
                "    color: #c00;\n" +
                "   }\n" +
                "   .string {\n" +
                "    background-color: #eef;\n" +
                "    color: #00f;\n" +
                "   }\n" +
                "   .charline {\n" +
                "    background-color: #eef;\n" +
                "    color: #00f;\n" +
                "   }\n" +
                "   .operator {\n" +
                "    background-color: #fff;\n" +
                "    color: #f30;\n" +
                "   }\n" +
                "   .delim {\n" +
                "    background-color: #fff0f0;\n" +
                "    color: #f00;\n" +
                "   }\n" +
                "   .reserved {\n" +
                "    background-color: #fff;\n" +
                "    color: #0000a0;\n" +
                "   }\n" +
                "   .num {\n" +
                "    background-color: #fff;\n" +
                "    color: #ff52f9;\n" +
                "   }\n" +
                "   .ident {\n" +
                "    background-color: #fff;\n" +
                "    color: #00a000;\n" +
                "   }\n" +
                "   .error {\n" +
                "    background-color: #f00;\n" +
                "    color: #fff;\n" +
                "   }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n");
        writer.write("<p>");

        writer.write("<span class =\"clear\">clear</span><br>");
        writer.write("<span class =\"white\">white</span><br>");
        writer.write("<span class =\"comment\">comment</span><br>");
        writer.write("<span class =\"directive\">directive</span><br>");
        writer.write("<span class =\"string\">string</span><br>");
        writer.write("<span class =\"charline\">charline</span><br>");
        writer.write("<span class =\"operator\">operator</span><br>");
        writer.write("<span class =\"delim\">delim</span><br>");
        writer.write("<span class =\"reserved\">reserved</span><br>");
        writer.write("<span class =\"num\">num</span><br>");
        writer.write("<span class =\"error\">error</span><br>");

        writer.write("<br>");


        int i = 0;
        while(i < len){
            int pos1 = i;
            int pos2 = text.indexOf("\r", pos1 + 1);
            if(pos2 == -1)
                pos2 = len;
            color etalon = token[pos1];
            String tmp = String.valueOf(text.charAt(pos1));
            for(int j = pos1 + 1; j < pos2; j++){
                if(token[j] == etalon)
                    if(text.charAt(j) == '<')
                        tmp += "&lt";
                    else{
                        if(text.charAt(j) == '>')
                            tmp += "&gt";
                        else
                            tmp += text.charAt(j);
                    }
                else {
                    writer.write("<span class = \""+get_html_class(etalon)+"\">" +
                            tmp + "</span>");
                    etalon = token[j];
                    tmp = String.valueOf(text.charAt(j));
                }
            }
            writer.write("<span class = \""+get_html_class(etalon)+"\">" +
                    tmp + "</span>");
            //  writer.write("<br>\n");
            i = pos2 + 1;
        }

        writer.write("</p>");
        writer.write("</body>\n" +
                "</html>");
        writer.close();
        System.out.println("html printed");
    }

    private String get_html_class(color col){
        return switch (col) {
            case clear -> "clear";
            case white -> "white";
            case comment -> "comment";
            case directive -> "directive";
            case string -> "string";
            case charline -> "charline";
            case operator -> "operator";
            case delim -> "delim";
            case reserved -> "reserved";
            case num -> "num";
            case ident -> "ident";
            case error -> "error";
            default -> throw new IllegalStateException("Unexpected value: " + col);
        };
    }





    //variables

    String fname;
    FileReader fr;
    BufferedReader reader;

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
        ident,
        error
    }

    //HashSet<Integer> white;
    String white = "\011\012\013\014\015\040";
    //HashSet<Integer> az;
    //HashSet<Integer> az09;
    //HashSet<Integer> az09_;
    String az = "";
    String az09 = "";
    String az09_ = "";
    String numeric = "";
    //HashSet<Integer> numbers;
    LinkedHashSet<String> operator;
    HashSet<String> reserved;
    HashSet<String> delim;

    String text = "";
    String text_draft = "";
    color[] token;
    int len;




}
