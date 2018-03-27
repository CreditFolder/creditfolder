package io.creditfolder.peer;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 16:25
 */
public class Peer extends Seed {
    /* 如果为True表示，等待远程节点响应 */
    private volatile boolean isRequesting = false;

    public Peer(InetSocketAddress address) {
        super(address);
    }

    public Peer(InetSocketAddress address, String name) {
        super(address, name);
    }

    public Peer(Seed seed) {
        super(seed.getAddress(), seed.getName());
    }

    public Peer(Seed seed, Socket socket) {
        super(seed.getAddress(), seed.getName());
        this.socket = socket;
    }

    public Peer(Socket socket) {
        super((InetSocketAddress)socket.getRemoteSocketAddress());
        this.socket = socket;
    }

    /* socket地址 */
    private transient Socket socket;
    /* 输入流 */
    private transient Scanner in;
    /* 输出流 */
    private transient PrintWriter out;

    public boolean isConnected() {
        if (socket == null) {
            return false;
        }
        return socket.isConnected();
    }

    public void request(JSONObject request) throws IOException {
        isRequesting = true;
        this.write(request);
    }

    public JSONObject getResponse() throws IOException, JSONException {
        JSONObject response = this.read();
        isRequesting = false;
        return response;
    }

    /**
     * 输出一个对象
     * @param object
     * @throws IOException
     */
    public void write(JSONObject object) throws IOException {
        if (out == null) {
            OutputStream outputStream = socket.getOutputStream();
            out = new PrintWriter(outputStream);
        }
        out.println(object.toString().trim());
        out.flush();
    }

    /**
     * 读取一个对象
     * @return
     */
    public JSONObject read() throws IOException, JSONException {
        if (in == null) {
            InputStream inputStream = socket.getInputStream();
            in = new Scanner(inputStream);
        }
        String message = in.nextLine();
        return new JSONObject(message);
    }

    /**
     * 断开与当前节点的连接
     * @throws IOException
     */
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    public boolean hasMessage() {
        if (in == null) {
            try {
                InputStream inputStream = socket.getInputStream();
                in = new Scanner(inputStream);
            }
            catch (IOException e) {
                return false;
            }
        }
        return in.hasNextLine();
    }

    public JSONObject toJSONObject() throws JSONException {
        return super.toJSONObject();
    }

    public static Peer parse(JSONObject jsonObject) throws JSONException {
        Seed seed = Seed.parse(jsonObject);
        return new Peer(seed);
    }

    public boolean isRequesting() {
        return isRequesting;
    }

    // IP + port想通，则表示是同一个连接
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Peer(" + getAddress().getAddress() + ":" + getAddress().getPort() + ")";
    }
}