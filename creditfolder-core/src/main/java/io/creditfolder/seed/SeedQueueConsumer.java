package io.creditfolder.seed;

import io.creditfolder.peer.PeerKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月29日 00:38
 */
public class SeedQueueConsumer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SeedQueueConsumer.class);
    private PeerKeeper peerKeeper;

    public SeedQueueConsumer(PeerKeeper peerKeeper) {
        this.peerKeeper = peerKeeper;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Seed seed = SeedKeeper.seedsQueue.take();
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
                peerKeeper.manageChannel(socketChannel, SelectionKey.OP_CONNECT, seed);
                InetSocketAddress address = new InetSocketAddress(seed.getIp(), seed.getPort());
                socketChannel.connect(address);
                peerKeeper.wakeup();
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
