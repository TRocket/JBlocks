package org.jblocks.scriptengine.js.impl;

/**
 * Simple JavaScript beautifier.
 * @see #work(java.lang.String)
 * @author ZeroLuck
 */
public class JsBeautifier {

    private JsBeautifier() {
    }

    /**
     * Creates a String with <i>n</i> whitespaces.
     */
    private static String createWhitespaceString(int cnt) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cnt; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * @param index the index of the JavaScript-String (has to be a '\"')
     * @param code the String
     * @return the end of the JavaScript-String (index in code, will be a '\"')
     */
    private static int skipString(int index, String code) {
        if (code.charAt(index) != '\"') {
            throw new IllegalStateException("Not the beginning of a String");
        }
        index++;
        char c;
        while ((c = code.charAt(index++)) != '\"') {
            if (c == '\\') {
                index++;
            }
        }
        return index;
    }

    /**
     * Beautifies JavaScript code. <br />
     * 
     * @param code the javascript code
     * @return the "beautified" code
     */
    public static String work(String code) {
        code = code.replace("\n", "");
        StringBuilder sb = new StringBuilder();
        int freespace = 0;
        int len = code.length();
        char last = 0;
        int insideExpression = 0;
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
            if (c != ' ' && last == ';' && insideExpression > 0) {
                sb.append(' ');
            }
            if (c == '(') {
                insideExpression++;
            }
            if (c == ')') {
                insideExpression--;
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
            } else if (c == ';' && insideExpression == 0) {
                if (last != '}') {
                    sb.append(";\n");
                    sb.append(createWhitespaceString(freespace));
                }
            } else if (c == '\"') {
                int end = skipString(i, code);
                sb.append(code.substring(i, end));
                i = end - 1;
            } else {
                sb.append(c);
            }

            last = c;
        }
        return sb.toString();
    }
}
