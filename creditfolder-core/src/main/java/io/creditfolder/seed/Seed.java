package io.creditfolder.seed;

import com.alibaba.fastjson.JSONObject;

import java.util.Objects;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:00
 */
public class Seed {
    private static final int STATE_UNCONNECT = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_DEAD = 3;
    private String ip;
    private int port;
    private int state;
    /* 每个节点的名称，完全是为了好记 */
    private String name;

    public Seed(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.state = STATE_UNCONNECT;
    }

    public Seed(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.state = STATE_UNCONNECT;
    }

    public boolean isUnConnected() {
        return state == STATE_UNCONNECT;
    }

    public boolean isConnecting() {
        return state == STATE_CONNECTING;
    }

    public boolean isConnected() {
        return state == STATE_CONNECTED;
    }

    public boolean isDead() {
        return state == STATE_DEAD;
    }

    public void unConnect() {
        this.state = STATE_UNCONNECT;
    }

    public void connecting() {
        this.state = STATE_CONNECTING;
    }

    public void connected() {
        this.state = STATE_CONNECTED;
    }

    public void dead() {
        this.state = STATE_DEAD;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        object.put("ip", ip);
        object.put("port", port);
        object.put("name", name);
        return object;
    }

    public static Seed parse(JSONObject jsonObject) {
        String ip = jsonObject.getString("ip");
        int port = jsonObject.getInteger("port");
        String name = jsonObject.getString("name");
        return new Seed(ip, port, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seed)) return false;
        Seed seed = (Seed) o;
        return ip.equals(seed.ip) && port == seed.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @Override
    public String toString() {
        return "Seed(" + ip + ":" + port + ")";
    }
}
