package de.mrkriskrisu.bot.threads;

import java.util.Vector;

public class Queue {

    public void add(Object o) {
	  synchronized (_queue) {
		_queue.addElement(o);
		_queue.notify();
	  }
    }

    public void addFront(Object o) {
	  synchronized (_queue) {
		_queue.insertElementAt(o, 0);
		_queue.notify();
	  }
    }

    public Object next() {

	  Object o = null;

	  synchronized (_queue) {
		if (_queue.size() == 0) {
		    try {
			  _queue.wait();
		    } catch (InterruptedException e) {
			  return null;
		    }
		}

		try {
		    o = _queue.firstElement();
		    _queue.removeElementAt(0);
		} catch (ArrayIndexOutOfBoundsException e) {
		    throw new InternalError("Race hazard in Queue object.");
		}
	  }

	  return o;
    }

    public boolean hasNext() {
	  return (this.size() != 0);
    }

    public void clear() {
	  synchronized (_queue) {
		_queue.removeAllElements();
	  }
    }

    public int size() {
	  return _queue.size();
    }

    private Vector<Object> _queue = new Vector<Object>();

}
