package org.jblocks.scriptengine;

import java.util.Map;

/**
 * The NativeBlock "API" for parsing parameters. <br />
 * 
 * @author ZeroLuck
 */
public class Parameters {

    private Parameters() {
    }

    public static String asString(Object o) {
        if (o == null) {
            return "";
        }
        return o.toString();
    }

    public static Map<String, Object> getVariablesForContext(Object context) {
        if (context instanceof Map) {
            return (Map<String, Object>) context;
        }
        if (context instanceof org.jblocks.scriptengine.impl.DefaultScriptThread.StackElement) {
            return ((org.jblocks.scriptengine.impl.DefaultScriptThread.StackElement) context).getVariables();
        }
        return null;
    }

    public static boolean asBoolean(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        if (o == null) {
            return false;
        }

        return (o.equals("true"));
    }

    public static int asInt(Object o) {
        if (o instanceof Integer) {
            return (Integer) o;
        }
        try {
            return Integer.valueOf(o + "");
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static long asLong(Object o) {
        if (o instanceof Long) {
            return (Long) o;
        }
        try {
            return Long.valueOf(o + "");
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static double asDouble(Object o) {
        if (o instanceof Double) {
            return (Double) o;
        }
        if (o instanceof Float) {
            return ((Float) o).doubleValue();
        }
        try {
            return Double.valueOf(o + "");
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static float asFloat(Object o) {
        if (o instanceof Float) {
            return (Float) o;
        }
        if (o instanceof Double) {
            return ((Double) o).floatValue();
        }
        try {
            return Float.valueOf(o + "");
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
