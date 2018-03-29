package io.creditfolder.peer;

import io.creditfolder.message.PeerMessage;
import io.creditfolder.seed.Seed;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 只有连接了的节点才叫Peer，没有连接的充其量只能叫Seed，哈哈哈哈
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 16:25
 */
public class Peer {
    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private static final Map<SelectionKey, Peer> allPeers = new ConcurrentHashMap<>();
    /* 专门用于读的buffer */
    private ByteBuffer readBuffer = ByteBuffer.allocateDirect(512);
    /* 专门用于写入的buffer */
    private ByteBuffer writeBuffer = null;
    /* socket地址 */
    private transient SelectionKey selectionKey;
    /* 目标ip */
    private String ip;
    /* 目标端口 */
    private int port;
    /* 目标节点 */
    private Seed seed;

    private void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public static Peer open(SelectionKey selectionKey) {
        Peer peer = new Peer();
        try {
            peer.setSelectionKey(selectionKey);
            SocketChannel channel = (SocketChannel)selectionKey.channel();
            InetSocketAddress address = (InetSocketAddress)channel.getRemoteAddress();
            peer.ip = address.getAddress().getHostAddress();
            peer.port = address.getPort();
            allPeers.put(selectionKey, peer);
            return peer;
        }
        catch (IOException e) {
            return null;
        }
    }

    public static Peer open(SelectionKey selectionKey, Seed seed) {
        Peer peer = Peer.open(selectionKey);
        peer.seed = seed;
        return peer;
    }

    public Seed getSeed() {
        return seed;
    }

    /**
     * 根据SelectionKey获取对应的节点
     * @param key
     * @return
     */
    public static Peer getFromCache(SelectionKey key) {
        return allPeers.get(key);
    }

    public boolean isConnected() {
        if (selectionKey == null) {
            return false;
        }
        return (selectionKey.channel().isOpen());
    }

    /**
     * 输出一个对象
     * @param object
     * @throws IOException
     */
    public void write(JSONObject object) throws IOException {
        byte[] content = object.toString().trim().getBytes();
        logger.info("write {} content: {}", this, object.toString());
        writeBuffer = ByteBuffer.allocateDirect(content.length);
        writeBuffer.put(content);
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
        selectionKey.selector().wakeup();
    }

    public void doWrite(SelectionKey key) throws IOException {
        if (writeBuffer == null) {
            return;
        }
        SocketChannel channel = (SocketChannel)key.channel();
        writeBuffer.flip();
        while (writeBuffer.hasRemaining()) {
            channel.write(writeBuffer);
        }
        writeBuffer.clear();
        writeBuffer = null;
        selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
    }

    public PeerMessage read(SelectionKey key) throws IOException, JSONException, PeerCloseException {
        readBuffer.clear();
        SocketChannel channel = (SocketChannel)key.channel();
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        while ((count = channel.read(readBuffer)) > 0) {
            readBuffer.flip();
            stringBuilder.append(StandardCharsets.UTF_8.decode(readBuffer).toString().trim());
            readBuffer.clear();
        }
        if (count == -1) {
            throw new PeerCloseException();
        }
        String content = stringBuilder.toString().trim();
        logger.info("receive {} content: {}", this, content);
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        return PeerMessage.build(new JSONObject(content), this);
    }

    /**
     * 断开与当前节点的连接
     * @throws IOException
     */
    public void close() throws IOException {
        allPeers.remove(selectionKey);
        selectionKey.channel().close();
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
        return "Peer(" + ip + ":" + port + ")";
    }
}