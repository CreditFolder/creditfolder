package io.creditfolder.peer;

import io.creditfolder.message.command.Message;
import io.creditfolder.message.MessageReader;
import io.creditfolder.message.MessageSerializer;
import io.creditfolder.message.ProtocolException;
import io.creditfolder.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.List;
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

    /* 专门用于写入的buffer */
    private ByteBuffer writeBuffer = null;
    /* socket地址 */
    private transient SelectionKey selectionKey;
    /* 读取消息 */
    private transient MessageReader messageReader = new MessageReader();
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
     * @param message
     * @throws IOException
     */
    public void write(Message message) {
        logger.info("write {} content: {}", this, message);
        byte[] content = MessageSerializer.serialize(message);
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

    public List<Message> read(SelectionKey key) throws IOException {
        try {
            List<Message> messageList = messageReader.read(key);
            for (Message message : messageList) {
                logger.info("receive {} content {}", this, message);
            }
            return messageList;
        }
        catch (ProtocolException | PeerCloseException e) {
            logger.error("{} read error", this);
            this.close();
            return Collections.emptyList();
        }
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