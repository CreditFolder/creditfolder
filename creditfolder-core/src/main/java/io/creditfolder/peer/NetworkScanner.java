package io.creditfolder.peer;

import io.creditfolder.message.command.PingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 连接状态和连接数量检查
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月27日 15:18
 */
@Component
public class NetworkScanner {
    private static final Logger logger = LoggerFactory.getLogger(NetworkScanner.class);

    @Autowired
    private PeerKeeper peerKeeper;
    @Autowired
    private PeerDiscovery peerDiscovery;

    public void startAsync() {
        new Thread() {
            public void run() {
                NetworkScanner.this.start();
            }
        }.start();
    }

    public void start() {
        logger.info("start to check peer alive");
        while (true) {
            List<Peer> peerList = peerKeeper.getAllPeers();
            for (Peer peer : peerList) {
                if (!isAlive(peer)) {
                    try {
                        peer.close();
                    } catch (IOException e) {
                        logger.error("peer close error");
                    }
                    peerKeeper.removePeer(peer);
                    logger.info("peer {} is closed", peer);
                }
            }
            peerDiscovery.findMorePeers();
            try {
                // 每隔一分钟检查一次
                Thread.sleep(60000);
            }
            catch (InterruptedException e) {
                logger.info("stop to check peer alive");
                break;
            }
        }
    }

    private boolean isAlive(Peer peer) {
        PingMessage message = new PingMessage();
        peer.write(message);
        return true;
    }
}