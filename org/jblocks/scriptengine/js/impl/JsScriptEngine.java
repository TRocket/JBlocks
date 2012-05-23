package org.jblocks.scriptengine.js.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.Block.Default;
import org.jblocks.scriptengine.ByobBlock;
import org.jblocks.scriptengine.IScript;
import org.jblocks.scriptengine.IScriptEngine;
import org.jblocks.scriptengine.IScriptThread;
import org.jblocks.scriptengine.NativeBlock;
import org.jblocks.scriptengine.impl.DefaultBlocks;

/**
 *
 * @author ZeroLuck
 */
public class JsScriptEngine implements IScriptEngine {

    private final List<JsScriptThread> threads = new ArrayList<JsScriptThread>();
    private final Map<String, Object> global = new HashMap<String, Object>();
    private final Map<Long, NativeBlock> natives = new HashMap<Long, NativeBlock>(100);
    private final List<IScriptEngine.ScriptEngineListener> listeners = new ArrayList<IScriptEngine.ScriptEngineListener>();
    private final ScriptEngine eng;
    private Set<String> varnames;
    
    public JsScriptEngine() {
        this(true);
    }

    public JsScriptEngine(boolean engine) {
        ScriptEngineManager mng = new ScriptEngineManager();
        if (engine) {
            eng = mng.getEngineByName("javascript");
        } else {
            eng = null;
        }
    }
    
    private String compileSequence(Object[] blocks, Set<Long> byobs, StringBuilder functions,
            String del, boolean delAtEnd) {
        
        final StringBuilder sb = new StringBuilder();
        int cnt = 0;
        for (final Object obj : blocks) {
            final boolean isEnd = !(cnt++ + 1 < blocks.length);

            if (obj instanceof ByobBlock) {
                final ByobBlock byob = (ByobBlock) obj;
                final long ID = byob.getID();
                if (!byobs.contains(ID)) {
                    final StringBuilder code = new StringBuilder();
                    code.append("function byob_").append(Math.abs(ID)).append("(");
                    for (int i = 0; i < byob.getParameterCount(); i++) {
                        code.append("param_").append(i);
                        if (i + 1 < byob.getParameterCount()) {
                            code.append(",");
                        }
                    }
                    code.append(") {");
                    code.append(compileSequence(byob.getSequence(), byobs, functions, ";", true));
                    code.append("}");
                    functions.append(code);
                    byobs.add(ID);
                }
                sb.append("byob_").append(Math.abs(ID)).append("(");
                sb.append(compileSequence(byob.getParameters(), byobs, functions, ",", false));
                sb.append(")").append(isEnd ? "" : del);
            } else if (obj instanceof NativeBlock) {
                final NativeBlock n = (NativeBlock) obj;
                final long ID = n.getID();
                if (ID == DefaultBlocks.FOR_ID) {
                    String rn = nextVarName();
                    sb.append("for(var ").append(rn).append(" = 0; ").append(rn).append(" < ");
                    sb.append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(";").append(rn).append("++) {");
                    sb.append(compileSequence((Object[]) n.getParameter(1), byobs, functions, ";", true));
                    sb.append("}");
                } else if (ID == DefaultBlocks.IF_ID) {
                    sb.append("if(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(") {");
                    sb.append(compileSequence((Object[]) n.getParameter(1), byobs, functions, ";", true));
                    sb.append("}");
                } else if (ID == DefaultBlocks.IF_ELSE_ID) {
                    sb.append("if(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(") {");
                    sb.append(compileSequence((Object[]) n.getParameter(1), byobs, functions, ";", true));
                    sb.append("} else {");
                    sb.append(compileSequence((Object[]) n.getParameter(2), byobs, functions, ";", true));
                    sb.append("}");
                } else if (ID == DefaultBlocks.RETURN_ID) {
                    sb.append("return ");
                    sb.append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append("");
                } else if (ID == DefaultBlocks.WHILE_ID) {
                    sb.append("while (");
                    sb.append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(") {");
                    sb.append(compileSequence((Object[]) n.getParameter(1), byobs, functions, ";", true));
                    sb.append("}");
                } else if (ID == DefaultBlocks.TRUE_ID) {
                    sb.append("true");
                } else if (ID == DefaultBlocks.FALSE_ID) {
                    sb.append("false");
                } else if (ID == DefaultBlocks.READ_PARAM_VARIABLE_ID) {
                    sb.append("param_").append(n.getParameter(0));
                } else if (ID == DefaultBlocks.WRITE_PARAM_VARIABLE_ID) {
                    sb.append("param_").append(n.getParameter(0)).append(" = (");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.READ_GLOBAL_VARIABLE_ID) {
                    sb.append("variables.get(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false)).append(")");
                } else if (ID == DefaultBlocks.WRITE_GLOBAL_VARIABLE_ID) {
                    sb.append("variables.put(");
                    sb.append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(",").append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.ADD_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" + ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.SUB_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" - ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.DIV_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" / ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.MUL_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" * ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.MOD_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" % ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.SMALLER_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" < ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.BIGGER_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" > ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.EQUALS_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" == ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.OR_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" || ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.AND_ID) {
                    sb.append("(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    sb.append(" && ");
                    sb.append(compileSequence(new Object[]{n.getParameter(1)}, byobs, functions, ",", false));
                    sb.append(")");
                } else if (ID == DefaultBlocks.NOT_ID) {
                    sb.append("!(").append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false)).append(")");
                } else if (ID == DefaultBlocks.RUN_ID) { // TODO
                    sb.append("eval(");
                    if (n.getParameter(0) != null) {
                        sb.append(compileSequence(new Object[]{n.getParameter(0)}, byobs, functions, ",", false));
                    } else {
                        sb.append("null");
                    }
                    sb.append(")");
                } else if (ID == DefaultBlocks.THE_SCRIPT_ID) { // TODO
                    sb.append("\"");
                    sb.append(compileSequence((Object[]) n.getParameter(0), byobs, functions, ";", true).replace("\"", "\\\""));
                    sb.append("\"");
                } else {
                    if (!natives.containsKey(ID)) {
                        natives.put(ID, n);
                    }
                    sb.append("native_").append(Math.abs(ID)).append(".evaluate(");
                    sb.append("null"); // <= context
                    if (n.getParameterCount() > 1) {
                        sb.append(",");
                    }
                    sb.append(compileSequence(n.getParameters(), byobs, functions, ",", false));
                    sb.append(")");
                }
            } else if (obj instanceof Object[]) {
                Object[] arr = (Object[]) obj;
                for (int i = 0; i < arr.length; i++) {
                    Object o = arr[i];
                    sb.append(compileSequence(new Object[]{o}, byobs, functions, del, delAtEnd ? true : i + 1 < arr.length));
                }
            } else if (obj instanceof String) {
                try {
                    sb.append(Integer.valueOf((String) obj));
                } catch (NumberFormatException ex) {
                    sb.append("\"");
                    sb.append(((String) obj).replace("\"", "\\\""));
                    sb.append("\"");
                }
            } else {
                sb.append(obj);
            }

            if (!isEnd || delAtEnd) {
                sb.append(del);
            }
        }
        return sb.toString();
    }

    private String nextVarName() {
        final String names = "ijkx";
        for (int i = 0; i < names.length(); i++) {
            String c = "" + names.charAt(i);
            if (!varnames.contains(c)) {
                varnames.add(c);
                return c;
            }
        }
        for (int i = 0;;i++) {
            String c = "v" + i;
            if (!varnames.contains(c)) {
                varnames.add(c);
                return c;
            }
        }
    }
    
    public synchronized String compileToJavaScriptCode(Block[] blocks) {
        varnames = new HashSet<String>(50);
        StringBuilder sb = new StringBuilder();
        StringBuilder end = new StringBuilder();
        if (blocks.length > 1) {
            sb.append("function run() {\n");
            sb.append(compileSequence(blocks, new HashSet<Long>(), end, ";", true));
            sb.append("}");
            sb.append(end);
            sb.append("\nrun();");
        } else {
            sb.append(compileSequence(blocks, new HashSet<Long>(), end, ";", true));
            sb.append(end);
        }
        return sb.toString();
    }

    @Override
    public IScript compile(Block[] blocks) {
        try {
            return compileJavaScript(compileToJavaScriptCode(blocks));
        } catch (ScriptException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't compile script!", ex);
        }
    }

    private JsScript compileJavaScript(String code) throws ScriptException {
        eng.put("variables", global);
        for (NativeBlock b : natives.values()) {
            eng.put("native_" + Math.abs(b.getID()), b);
        }
        Compilable c = (Compilable) eng;
        CompiledScript script = c.compile(code);
        return new JsScript(script);
    }

    @Override
    public Block getDefaultBlock(Default def) {
        return DefaultBlocks.getDefaultBlock(def);
    }

    @Override
    public IScriptThread execute(IScript s) {
        JsScriptThread t = new JsScriptThread(((JsScript) s).code, this);
        t.start();

        synchronized (listeners) {
            for (ScriptEngineListener l : listeners) {
                l.started(t);
            }
        }

        return t;
    }

    @Override
    public IScriptThread[] getThreads() {
        synchronized (threads) {
            return threads.toArray(new JsScriptThread[]{});
        }
    }

    @Override
    public Map<String, Object> getGlobalVariables() {
        return global;
    }

    @Override
    public void addListener(ScriptEngineListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(ScriptEngineListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    void notifyFinished(JsScriptThread t, Throwable err) {
        synchronized (threads) {
            threads.remove(t);
        }
        synchronized (listeners) {
            for (ScriptEngineListener l : listeners) {
                l.finished(t, err);
            }
        }
    }

    static class JsScript implements IScript {

        private CompiledScript code;

        public JsScript(CompiledScript s) {
            code = s;
        }
    }
}
