package io.creditfolder.peer;

import io.creditfolder.config.Config;
import io.creditfolder.message.MessageSerializer;
import io.creditfolder.message.command.Message;
import io.creditfolder.message.command.PingMessage;
import io.creditfolder.net.IPUtil;
import io.creditfolder.seed.Seed;
import io.creditfolder.seed.SeedKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
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
    private PeerKeeper peerKeeper;
    @Autowired
    private SeedKeeper seedKeeper;

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
            serverSocket.bind(new InetSocketAddress(Config.SERVER_PORT));
            SelectionKey selectionKey = peerKeeper.manageChannel(serverSocketChannel, SelectionKey.OP_ACCEPT, this);
            this.selectionKey = selectionKey;
            logger.info("peerServer start success at port:{}", Config.SERVER_PORT);
            initMySeed();
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
        if (peerKeeper.getInConnectCount() >= Config.MAX_IN_CONNECT) {
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

    public void initMySeed() {
        String ip = IPUtil.getExternalIp();
        SocketAddress address = new InetSocketAddress(ip, Config.SERVER_PORT);
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(3000);
            socket.connect(address, 30000);
            PingMessage ping = new PingMessage();
            byte[] content = MessageSerializer.serialize(ping);
            socket.getOutputStream().write(content);
            byte[] receive = new byte[1024];
            int receiveCount = socket.getInputStream().read(receive);
            byte[] pong = new byte[receiveCount];
            MessageSerializer.parseMessage(ByteBuffer.wrap(pong));
            seedKeeper.setSeedMyself(new Seed(ip, Config.SERVER_PORT, Config.MY_SEED_NAME));
            return;
        }
        catch (SocketTimeoutException e) {
            logger.info("external ip dected failed, external ip:{} is not bind to the host");
        }
        catch (Exception e) {
            logger.info("peerServer init myServer address {}", e);
        }
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            seedKeeper.setSeedMyself(new Seed(ip, Config.SERVER_PORT, Config.MY_SEED_NAME));
        }
        catch (UnknownHostException e) {

        }
    }
}