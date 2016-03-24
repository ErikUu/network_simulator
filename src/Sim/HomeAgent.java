package Sim;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by erikuusitalo on 25/02/16.
 */

public class HomeAgent{

    private Map<String, String> ipMap = new HashMap<String, String>();
    private Map<Node, String> hoaMap = new HashMap<Node, String>();

    private Buffer<Message> buffer;

    public HomeAgent(){}

    public HomeAgent(int buffterSize, int TTL) {
        this();
        buffer = new Buffer<>(buffterSize, TTL);
    }

    /**
     * Register home address for given node
     * @param n
     * @param id
     */
    public void registerHomeAddress(Node n, NetworkAddr id){
        if (hoaMap.get(n) == null)
            hoaMap.put(n, toIp(id));

    }

    /**
     * Register new location for moved node
     * @param n
     * @param newId
     */
    public void registerNewLocation(Node n, NetworkAddr newId){
        String cNewId = toIp(newId);
        ipMap.put(hoaMap.get(n), cNewId);
    }

    /**
     * Gets the care of address for given network address
     * @param id
     * @return CoA if exist, else null
     */
    public NetworkAddr getCoa(NetworkAddr id){

        String cid    = toIp(id);
        String mapId = ipMap.get(cid);

        if(compareId(mapId, cid) == false && mapId != null)
            return toNetworkAddr(ipMap.get(cid));
        return null;

    }

    /**
     * Converts network address to string
     * @param id
     * @return network address as a string
     */
    private String toIp(NetworkAddr id){
        String networkId = Integer.toString(id.networkId());
        String nodeId    = Integer.toString(id.nodeId());
        return networkId + "." + nodeId;
    }

    /**
     * Coverts string to network address
     * @param id
     * @return network address
     */
    private NetworkAddr toNetworkAddr(String id){
        String[] fn = id.split("\\.");
        return new NetworkAddr(Integer.parseInt(fn[0]), Integer.parseInt(fn[1]));
    }

    /**
     * Compares network addresses
     * @param mapId
     * @param id
     * @return
     */
    private boolean compareId(String mapId, String id){
        if(mapId == id)
            return true;
        return false;
    }

}


