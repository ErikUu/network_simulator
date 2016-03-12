package Sim;

/**
 * Created by erikuusitalo^tm on 03/03/16.
 */
public class RouterSolicitation implements Event {

    private NetworkAddr _source;

    public RouterSolicitation(NetworkAddr _source) {
        this._source = _source;
    }

    public NetworkAddr getSource() {
        return _source;
    }

    @Override
    public void entering(SimEnt locale) {

    }

}
