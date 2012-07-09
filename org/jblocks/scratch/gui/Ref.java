package org.jblocks.scratch.gui;

public class Ref {
public Ref(int pos) {
		this.pos = pos;
	}

int pos;

public int getPos() {
	return pos;
}

public String toString() {
    return "Ref(" + this.pos + ")";
}

}
