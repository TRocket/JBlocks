package org.jblocks.scriptengine.js.impl;

import javax.script.CompiledScript;
import org.jblocks.scriptengine.IScriptThread;

/**
 *
 * @author ZeroLuck
 */
class JsScriptThread extends Thread implements IScriptThread {

    private final CompiledScript script;
    private final JsScriptEngine eng;
    private Object retValue;

    public JsScriptThread(CompiledScript s, JsScriptEngine eng) {
        this.script = s;
        this.setPriority(MIN_PRIORITY);
        this.eng = eng;
    }

    @Override
    public void run() {
        try {
            retValue = script.eval();
            eng.notifyFinished(this, null);
        } catch (Throwable ex) {
            ex.printStackTrace();
            eng.notifyFinished(this, ex);
        }
    }

    @Override
    public Object getReturnValue() {
        return retValue;
    }
}
