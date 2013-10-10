package com.osum.bot.interfaces;

/**
 * User: Marty
 * Date: 3/30/13
 * Time: 3:16 PM
 */
public interface Node {

    int getId();

    Node getNext();

    Node getPrevious();

}
