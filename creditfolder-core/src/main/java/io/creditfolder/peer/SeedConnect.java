package io.creditfolder.peer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.Socket;

/**
 * 点对点连接
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 15:11
 */
class SeedConnect implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SeedConnect.class);
    private Seed seed;

    private PeerKeeper peerKeeper;

    public SeedConnect(Seed seed, PeerKeeper peerKeeper) {
        this.seed = seed;
        this.peerKeeper = peerKeeper;
    }

    @Override
    public void run() {
        try {
            logger.info("start to connect seed: {}", seed);
            Socket socket = connect(seed);

            logger.info("start to connect success: {}", seed);
            peerKeeper.addOutConnect(new Peer(seed, socket));
        }
        catch (IOException e) {
            logger.error("connect seed failed, seed: {}", seed, e);
        }
    }

    public Socket connect(Seed seed) throws IOException {
        Socket socket = new Socket();
        // 设置读取10s超时
        socket.setSoTimeout(10000);
        // 连接1s超时
        socket.connect(seed.getAddress(), 1000);
        return socket;
    }
}