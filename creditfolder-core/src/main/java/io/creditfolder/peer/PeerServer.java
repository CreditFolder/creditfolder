package io.creditfolder.peer;

import io.creditfolder.config.NetworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

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

    private SelectionKey selectionKey = null;
    private ServerSocket serverSocket = null;
    private ServerSocketChannel serverSocketChannel = null;
    private volatile boolean isRunning = false;

    /**
     * 启动Peer，等待其他节点连接
     */
    public void start() {
        if (isRunning) {
            logger.info("peerServer is already running, can't start again");
            return;
        }
        try {
            if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                logger.info("peerServer is already running, can't start again");
                return;
            }
            isRunning = true;
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(networkConfig.getServerPort()));
            SelectionKey selectionKey = peerKeeper.manageChannel(serverSocketChannel, SelectionKey.OP_ACCEPT, this);
            this.selectionKey = selectionKey;
            logger.info("peerServer start success at port:{}", networkConfig.getServerPort());
        }
        catch (IOException e) {
            logger.error("PeerServer start failed", e);
        }
    }

    public void newConnect(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        SelectionKey socketKey = peerKeeper.manageChannel(socketChannel, SelectionKey.OP_READ, null);
        Peer peer = Peer.open(socketKey);
        peerKeeper.addInConnect(peer);
        socketKey.attach(peer);
        logger.info("a peer connected:{}", peer);
        // 如果大于当前最大连接数，则停止peerServer服务
        if (peerKeeper.getInConnectCount() >= networkConfig.getMaxInConnect()) {
            this.stop();
        }
    }

    /**
     * 结束服务销毁socket
     */
    public void stop() {
        if (!isRunning) {
            logger.info("peer server is already stoped, can't stop again");
            return;
        }
        isRunning = false;
        if (selectionKey != null && selectionKey.isValid()) {
            selectionKey.cancel();
        }
        try {
            if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                serverSocketChannel.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
        catch (IOException e) {
            logger.error("serverSocket close failed", e);
        }
    }
}