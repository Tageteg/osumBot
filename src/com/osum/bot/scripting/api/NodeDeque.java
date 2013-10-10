package com.osum.bot.scripting.api;

import com.osum.bot.interfaces.Node;

/**
 * User: Marty
 * Date: 4/1/13
 * Time: 12:00 PM
 */
public class NodeDeque<T>
{

    private Node front;

    private com.osum.bot.interfaces.NodeDeque deque;


    public NodeDeque(com.osum.bot.interfaces.NodeDeque deq) {
        this.deque = deq;
    }

    @SuppressWarnings("unchecked")
	public T front() {
        Node node = deque.getTail().getNext();

        if (node == deque.getTail()) {
            front = null;
            return null;
        }

        front = node.getNext();
        return (T)front;
    }

    @SuppressWarnings("unchecked")
    public T tail() {
        Node node = deque.getTail().getPrevious();

        if (node == deque.getTail()) {
            front = null;
            return null;
        }

        front = node.getPrevious();
        return (T)front;
    }

    @SuppressWarnings("unchecked")
    public T next() {
        Node node = front;

        if (node == deque.getTail()) {
            front = null;
            return null;
        }

        front = node.getNext();

        return (T)front;
    }

    public int size() {
        int size = 0;

        Node n = deque.getTail().getPrevious();

        while(n != deque.getTail()) {
            n = n.getPrevious();
            size++;
        }
        return size;
    }

}
