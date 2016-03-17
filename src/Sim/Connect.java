package Sim;

/**
 * Created by erikuusitalo on 17/03/16.
 */
public class Connect implements Event {

    SimEnt link;

    public Connect(SimEnt link) {
        this.link = link;
    }

    public SimEnt getLink() {
        return link;
    }

    @Override
    public void entering(SimEnt locale) {

    }
}
