package io.creditfolder.peer;

import io.creditfolder.config.Config;
import io.creditfolder.message.command.Message;
import io.creditfolder.message.MessageHandler;
import io.creditfolder.seed.Seed;
import io.creditfolder.seed.SeedKeeper;
import io.creditfolder.seed.SeedQueueConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

/**
 * 管理所有的连接
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:16
 */
@Component
public class PeerKeeper {
    private static final Logger logger = LoggerFactory.getLogger(PeerKeeper.class);

    private volatile List<Peer> outConnectList = new ArrayList<>();
    private volatile List<Peer> inConnectList = new ArrayList<>();
    @Autowired
    private PeerServer peerServer;
    @Autowired
    private PeerDiscovery peerDiscovery;
    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private SeedKeeper seedKeeper;
    @Autowired
    private SeedQueueConsumer seedQueueConsumer;
    @Autowired
    private NetworkScanner networkScanner;

    private Selector selector;

    public SelectionKey manageChannel(SelectableChannel channel, int ops, Object attachment) throws ClosedChannelException {
        return channel.register(selector, ops, attachment);
    }

    /**
     * 连接种子节点
     * 启动三个线程：
     * 1. 启动本地节点等待新节点连接
     * 2. 检查已连接的节点是否连接正常
     * 3. 每个节点的消息循环
     */
    public void start() {
        // 初始化自己的节点信息
        try {
            selector = SelectorProvider.provider().openSelector();
        }
        catch (IOException e) {
            logger.info("peerkeeper start failed", e);
        }
        logger.info("peerkeeper start");

        seedKeeper.init();
        // 本地服务启动
        peerServer.start();
        // 开始消费阻塞队列中等待连接的Seed
        seedQueueConsumer.startAsync();

        // 连接所有的种子节点
        connectSuperSeed();

        // 检查连接状态
        networkScanner.startAsync();

        messageLoop();
    }

    public void messageLoop() {
        logger.info("start to message loop");
        while (true) {
            try {
                int count = selector.select();
                if (count <= 0) {
                    continue;
                }
            }
            catch (IOException e) {
                logger.error("select.select() error", e);
                continue;
            }

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        peerServer.newConnect(key);
                    }
                    else if (key.isConnectable()) {
                        onSeedConnected(key);
                    }
                    else if (key.isWritable()) {
                        Peer peer = (Peer) key.attachment();
                        peer.doWrite(key);
                    }
                    else {
                        Peer peer = Peer.getFromCache(key);

                        List<Message> messageList = peer.read(key);
                        for (Message message : messageList) {
                            messageHandler.handle(message, peer);
                        }
                    }
                }
                catch (IOException e) {
                    logger.error("messageLoop error", e);
                }
            }
        }
    }

    /**
     * 连接所有的种子节点
     */
    private void connectSuperSeed() {
        if (Config.ISGENESIS) {
            logger.info("this peer is genesisSeed, don't connect any peer");
            return;
        }
        for (Seed seed : Config.SUPERSEEDLIST) {
            if (!SeedKeeper.newSeedsQueue.offer(seed)) {
                return;
            }
        }
    }

    private void onSeedConnected(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        // 不管了
        try {
            if (!channel.finishConnect()) {
                key.cancel();
                logger.info("connect {} failed", key.attachment());
                return;
            }
        }
        catch (ConnectException e) {
            key.cancel();
            logger.info("connect {} failed", key.attachment());
            return;
        }
        Seed seed = (Seed)key.attachment();
        Peer peer = Peer.open(key, seed);
        addOutConnect(peer);
        key.attach(peer);
        key.interestOps(SelectionKey.OP_READ);
        logger.info("peer connected success:{}", peer);
    }

    /**
     * 将被动连接增加到peerkeeper
     * @param peer
     */
    void addInConnect(Peer peer) {
        if (inConnectList.size() >= Config.MAX_IN_CONNECT) {
            try {
                peer.close();
            }
            catch (IOException e) {
                logger.error("close peer error");
            }
            peerServer.stop();
        }
        else {
            inConnectList.add(peer);
        }
    }

    /**
     * 增加主动连接增加到peerKeeper
     * @param peer
     */
    void addOutConnect(Peer peer) {
        if (outConnectList.size() >= Config.MAX_OUT_CONNECT) {
            try {
                peer.close();
            }
            catch (IOException e) {
                logger.error("close peer error");
            }
        }
        else {
            outConnectList.add(peer);
            seedKeeper.saveSeed(peer.getSeed());
        }
    }

    /**
     * 获取被动连接的数量
     * @return
     */
    int getInConnectCount() {
        return inConnectList.size();
    }

    int getOutConnectCount() {
        return outConnectList.size();
    }

    /**
     * 获取所有的连接
     * @return
     */
    public List<Peer> getAllPeers() {
        List<Peer> peerList = new ArrayList<>();
        peerList.addAll(outConnectList);
        peerList.addAll(inConnectList);
        return peerList;
    }

    /**
     * 断开连接
     * @param peer
     */
    public void removePeer(Peer peer) {
        if (outConnectList.contains(peer)) {
            outConnectList.remove(peer);
            logger.info("remove peer {}", peer);
        }
        if (inConnectList.contains(peer)) {
            inConnectList.remove(peer);
            logger.info("remove peer {}", peer);
        }
        if (Config.MIN_IN_CONNECT < inConnectList.size()) {
            peerServer.start();
        }
        if (Config.MIN_OUT_CONNECT < outConnectList.size()) {
//            peerDiscovery.findMorePeers();
        }
    }

    public List<Peer> getOutConnectList() {
        return outConnectList;
    }

    public List<Peer> getInConnectList() {
        return inConnectList;
    }

    public void wakeup() {
        this.selector.wakeup();
    }
}