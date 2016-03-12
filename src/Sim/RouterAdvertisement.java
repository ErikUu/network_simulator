package Sim;

/**
 * Created by erikuusitalo on 03/03/16.
 */
public class RouterAdvertisement implements Event {

    private int _freeInterfaces = 0;

    public RouterAdvertisement(int _freeInterfaces) {
        this._freeInterfaces = _freeInterfaces;
    }

    public int getFreeInterfaces() {
        return _freeInterfaces;
    }

    @Override
    public void entering(SimEnt locale) {

    }
}
