package io.creditfolder.peer;

import io.creditfolder.config.NetworkConfig;
import io.creditfolder.message.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private NetworkConfig networkConfig;
    @Autowired
    private PeerDiscovery peerDiscovery;
    @Autowired
    private MessageHandler messageHandler;

    /**
     * 连接种子节点
     * 启动三个线程：
     * 1. 启动本地节点等待新节点连接
     * 2. 检查已连接的节点是否连接正常
     * 3. 每个节点的消息循环
     */
    public void start() {
        logger.info("peerkeeper start");
        // 连接所有的种子节点
        connectAllSeedAsync();

        // 本地服务启动
        peerServer.startAsync();

        // 检查连接状态
//        startPeerAliveCheckAsync();
        logger.info("peerkeeper start finished");
    }

    /**
     * 连接所有的种子节点
     */
    private void connectAllSeedAsync() {
        if (networkConfig.isSeed()) {
            logger.info("this is seed, don't connect any peer");
            return;
        }
        List<Seed> seedList = networkConfig.getAllSeed();
        connectSeedList(seedList);
    }

    private void connectSeedList(List<Seed> seedList) {
        for (Seed seed : seedList) {
            if (outConnectList.size() < networkConfig.getMaxOutConnect()) {
                Thread thread = new Thread(new SeedConnect(seed, this, messageHandler));
                thread.setName(seed.toString());
                thread.start();
            }
        }
    }

    /**
     * 检查节点连接是否正常
     */
    private void startPeerAliveCheckAsync() {
        Thread thread = new Thread(new PeerAliveCountChecker(this, peerDiscovery));
        thread.start();
    }

    /**
     * 将被动连接增加到peerkeeper
     * @param peer
     */
    void addInConnect(Peer peer) {
        if (inConnectList.size() >= networkConfig.getMaxInConnect()) {
            try {
                peer.close();
            }
            catch (IOException e) {
                logger.error("close peer error");
            }
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
        if (outConnectList.size() >= networkConfig.getMaxOutConnect()) {
            try {
                peer.close();
            }
            catch (IOException e) {
                logger.error("close peer error");
            }
        }
        else {
            outConnectList.add(peer);
        }
    }

    /**
     * 获取被动连接的数量
     * @return
     */
    int getInConnectCount() {
        return inConnectList.size();
    }

    /**
     * 获取所有的连接
     * @return
     */
    public List<Peer> getAllConnect() {
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
        if (networkConfig.getMinInConnect() < inConnectList.size()) {
            peerServer.startAsync();
        }
        if (networkConfig.getMinOutConnect() < outConnectList.size()) {
            peerDiscovery.connectMorePeer();
        }
    }

    public List<Peer> getOutConnectList() {
        return outConnectList;
    }

    public List<Peer> getInConnectList() {
        return inConnectList;
    }
}
