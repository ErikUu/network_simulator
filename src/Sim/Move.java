package Sim;

import sun.nio.ch.Net;

/**
 * Created by erikuusitalo on 19/02/16.
 */
public class Move implements Event {

    private Node node;
    private int newInterface;
    private NetworkAddr newId;


    public Move(Node node, int newInterface, NetworkAddr newId) {
        this.node = node;
        this.newInterface = newInterface;
        this.newId = newId;
    }

    public Node getNode() {
        return node;
    }

    public int getNewInterface() {
        return newInterface;
    }

    public NetworkAddr getId() {
        return newId;
    }

    public void entering(SimEnt locale) {

    }

}
