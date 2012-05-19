package org.jblocks.scriptengine.js.impl;

/**
 *
 * @author ZeroLuck
 */
public class JsBeautifier {

    private static String createWhitespaceString(int cnt) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cnt; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static int skipString(int index, String code) {
        if (code.charAt(index) != '\"') {
            throw new IllegalStateException("Not the beginning of a String");
        }
        index++;
        char c;
        while ((c = code.charAt(index++)) != '\"') {
            if (c == '\\') {
                index += 2;
            }
        }
        return index;
    }

    public static String work(String code) {
        code = code.replace("\n", "");
        StringBuilder sb = new StringBuilder();
        int freespace = 0;
        int len = code.length();
        char last = 0;
        for (int i = 0; i < len; i++) {
            char c = code.charAt(i);
            if (last == ',' && c != ' ') {
                sb.append(' ');
            }
            if (c == '(' && last != ' ' && last != '(') {
                sb.append(' ');
            }
            if (c == '{' && last != ' ') {
                sb.append(' ');
            }

            if (c == '{') {
                freespace += 4;
                sb.append("{\n");
                sb.append(createWhitespaceString(freespace));
            } else if (c == '}') {
                freespace -= 4;
                sb.delete(sb.length() - 4, sb.length());
                sb.append("}\n");
                sb.append(createWhitespaceString(freespace));
            } else if (c == ';') {
                if (last != '}') {
                    sb.append(";\n");
                    sb.append(createWhitespaceString(freespace));
                }
            } else if (c == '\"') {
                int end = skipString(i, code);
                sb.append(code.substring(i, end + 1));
                i = end;
            } else {
                sb.append(c);
            }

            last = c;
        }
        return sb.toString();
    }
    
}
