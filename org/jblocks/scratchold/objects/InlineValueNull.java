package org.jblocks.scratchold.objects;
//no construct!

/**
 * n/a (constant value)
 * @author TRocket
 *
 */
public class InlineValueNull extends InlineValue {
static final Object nullval = null;

@SuppressWarnings("unchecked")
@Override
public Class<Object> getValue() {
	// TODO Auto-generated method stub
	return (Class<Object>) nullval;//it works
}
}
