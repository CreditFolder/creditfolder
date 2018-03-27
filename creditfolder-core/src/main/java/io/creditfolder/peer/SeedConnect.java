package io.creditfolder.peer;

import io.creditfolder.message.MessageHandler;
import io.creditfolder.message.MessageProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

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

    private MessageHandler messageHandler;

    public SeedConnect(Seed seed, PeerKeeper peerKeeper, MessageHandler messageHandler) {
        this.seed = seed;
        this.peerKeeper = peerKeeper;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            logger.info("start to connect seed: {}", seed);
            Socket socket = connect();

            logger.info("start to connect success: {}", seed);
            Peer peer = new Peer(seed, socket);
            peerKeeper.addOutConnect(peer);
            // 连接成功跟直接进入消息循环，如果再创建一个线程，当这个县城结束之后，子进程也会结束
            MessageProcess messageProcess = new MessageProcess(peer, messageHandler, peerKeeper);
            messageProcess.run();
        }
        catch (IOException e) {
            logger.error("connect seed failed, seed: {}", seed, e);
        }
    }

    public Socket connect() throws IOException {
        Socket socket = new Socket();
        // 设置读取10s超时
        socket.setSoTimeout(10000);
        // 连接1s超时
        socket.connect(seed.getAddress(), 1000);
        return socket;
    }
}