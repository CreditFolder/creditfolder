package io.creditfolder.peer;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * 连接状态和连接数量检查
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月27日 15:18
 */
public class PeerAliveCountChecker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PeerAliveCountChecker.class);

    private PeerKeeper peerKeeper;

    private PeerDiscovery peerDiscovery;

    public PeerAliveCountChecker(PeerKeeper peerKeeper, PeerDiscovery peerDiscovery) {
        this.peerKeeper = peerKeeper;
        this.peerDiscovery = peerDiscovery;
    }

    @Override
    public void run() {
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
        try {
            JSONObject pingMessage = new JSONObject();
            pingMessage.put("command", "ping");
            peer.write(pingMessage);
            return true;
        }
        catch (JSONException e) {
            logger.error("peer {} alive throw jsonexception", peer, e);
            return false;
        }
        catch (IOException e) {
            logger.error("peer is closed: peer{}", peer);
            return false;
        }
    }
}