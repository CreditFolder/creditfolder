package io.creditfolder.rpc;

import io.creditfolder.config.NetworkConfig;
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
 * @since 2018年03月27日 16:25
 */
@Component
public class RPCServer {
    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);
    @Autowired
    private NetworkConfig networkConfig;
    private ServerSocket serverSocket = null;
    private volatile boolean isRunning = false;
    @Autowired
    private RPCMessageHandler messageHandler;

    public void startAsync() {
        new Thread() {
            @Override
            public void run() {
                RPCServer.this.start();
            }
        }.start();
    }

    public void start() {
        try {
            if (serverSocket == null || serverSocket.isClosed()) {
                serverSocket = new ServerSocket(networkConfig.getRPCServerPort());
                serverSocket.setReuseAddress(true);
            }
            isRunning = true;
            logger.info("rpc server is start at port: {}", networkConfig.getRPCServerPort());
            while (isRunning) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new RPCMessageProcess(messageHandler, socket));
                thread.start();
            }
        }
        catch (IOException e) {
            logger.error("rpc server start failed", e);
        }
    }

    public void stop() {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                this.serverSocket.close();
            }
            catch (IOException e) {
                logger.error("serverSocket.close() error", e);
            }
        }
    }
}
