package io.creditfolder.seed;

import io.creditfolder.db.SeedStoreProvider;
import io.creditfolder.peer.PeerDiscovery;
import io.creditfolder.peer.PeerKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月29日 00:38
 */
@Component
public class SeedQueueConsumer {
    private static final Logger logger = LoggerFactory.getLogger(SeedQueueConsumer.class);
    @Autowired
    private PeerKeeper peerKeeper;
    @Autowired
    private SeedKeeper seedKeeper;
    @Autowired
    private SeedStoreProvider seedStoreProvider;
    @Autowired
    private PeerDiscovery peerDiscovery;

    public SeedQueueConsumer(PeerKeeper peerKeeper) {
        this.peerKeeper = peerKeeper;
    }

    public void startAsync() {
        new Thread() {
            public void run() {
                SeedQueueConsumer.this.start();
            }
        }.start();
    }

    public void start() {
        logger.info("seedQueueConsumer start");
        try {
            while (true) {
                Seed seed = SeedKeeper.newSeedsQueue.take();
                seedStoreProvider.store(seed);
                if (peerDiscovery.needMoreOutPeers() && !seed.equals(seedKeeper.getSeedMyself()) && !seedKeeper.hasExist(seed)) {
                    SocketChannel socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(false);
                    peerKeeper.manageChannel(socketChannel, SelectionKey.OP_CONNECT, seed);
                    InetSocketAddress address = new InetSocketAddress(seed.getIp(), seed.getPort());
                    socketChannel.connect(address);
                    peerKeeper.wakeup();
                }
            }
        }
        catch (IOException e) {
            logger.error("SeedQueueConsumer thread error", e);
        }
        catch (InterruptedException e) {
            logger.error("seedQueueConsumer is interrupted", e);
        }
    }
}
