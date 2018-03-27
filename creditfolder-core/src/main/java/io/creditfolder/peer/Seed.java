package io.creditfolder.peer;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:00
 */
public class Seed {
    /* IP + 端口 */
    private InetSocketAddress address;
    /* 每个节点的名称，完全是为了好记 */
    private String name;

    public Seed(InetSocketAddress address) {
        this.address = address;
    }

    public Seed(InetSocketAddress address, String name) {
        this.address = address;
        this.name = name;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("ip", getAddress().getAddress().toString());
        object.put("port", getAddress().getPort());
        object.put("name", getName());
        return object;
    }

    public static Peer parse(JSONObject jsonObject) throws JSONException {
        String ip = jsonObject.getString("ip");
        int port = jsonObject.getInt("port");
        String name = jsonObject.getString("name");
        InetSocketAddress address = new InetSocketAddress(ip, port);
        Peer peer = new Peer(address, name);
        return peer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seed)) return false;
        Seed seed = (Seed) o;
        return seed.getAddress().getAddress().toString().equals(getAddress().getAddress().toString())
                && seed.getAddress().getPort() == getAddress().getPort();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress().getAddress().toString(), getAddress().getPort());
    }

    @Override
    public String toString() {
        return "Seed(" + getAddress().getAddress() + ":" + getAddress().getPort() + ")";
    }
}
