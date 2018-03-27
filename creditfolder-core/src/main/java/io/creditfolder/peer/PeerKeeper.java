package io.creditfolder.peer;

import io.creditfolder.config.NetworkConfig;
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

    /**
     * 进入网络，启动本地节点，并从种子节点中获得连接
     */
    public void start() {
        // 连接所有的种子节点
        connectAllSeedAsync();

        // 本地服务启动
        peerServer.startAsync();
    }

    /**
     * 连接所有的种子节点
     */
    private void connectAllSeedAsync() {
        List<Seed> seedList = networkConfig.getAllSeed();
        for (Seed seed : seedList) {
            if (outConnectList.size() < networkConfig.getMaxOutConnect()) {
                Thread thread = new Thread(new SeedConnect(seed, this));
                thread.start();
            }
        }
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
        }
        if (inConnectList.contains(peer)) {
            inConnectList.remove(peer);
        }
    }
}
