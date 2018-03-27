package io.creditfolder.peer;

import io.creditfolder.config.NetworkConfig;
import io.creditfolder.message.MessageHandler;
import io.creditfolder.message.MessageProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:25
 */
@Component
public class PeerServer {
    private static final Logger logger = LoggerFactory.getLogger(PeerServer.class);

    @Autowired
    private NetworkConfig networkConfig;
    @Autowired
    private PeerKeeper peerKeeper;
    @Autowired
    private MessageHandler messageHandler;
    private volatile ServerSocket serverSocket = null;
    private volatile boolean isRunning = false;

    public void startAsync() {
        new Thread() {
            public void run() {
                PeerServer.this.start();
            }
        }.start();
    }

    /**
     * 启动Peer，等待其他节点连接
     */
    public void start() {
        if (isRunning) {
            logger.info("peerServer is already running");
            return;
        }
        try {
            if (serverSocket == null || serverSocket.isClosed()) {
                serverSocket = new ServerSocket(networkConfig.getServerPort());
            }
            isRunning = true;
            logger.info("peerServer start success at port:{}", networkConfig.getServerPort());
            while (peerKeeper.getInConnectCount() < networkConfig.getMaxInConnect()) {
                Socket incoming = serverSocket.accept();
                Peer peer = new Peer(incoming);
                logger.info("A peer connected {}", peer);
                peerKeeper.addInConnect(peer);
                Thread thread = new Thread(new MessageProcess(peer, messageHandler, peerKeeper));
                thread.start();
            }
            if (peerKeeper.getInConnectCount() >= networkConfig.getMaxInConnect()) {
                isRunning = false;
                logger.info("peerServer has stoped");
                serverSocket.close();
            }
        }
        catch (IOException e) {
            logger.error("PeerServer start failed", e);
        }
    }

    /**
     * 结束服务销毁socket
     */
    public void stop() {
        isRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            }
            catch (IOException e) {
                logger.error("serverSocket close failed", e);
            }
        }
    }

    public static void main(String args[]) {
        PeerServer peerServer = new PeerServer();
        peerServer.startAsync();
        logger.info("start finish");
    }
}