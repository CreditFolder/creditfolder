package io.creditfolder.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月27日 16:56
 */
public class RPCMessageProcess implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RPCMessageProcess.class);
    private RPCMessageHandler messageHandler;

    private Socket socket;

    private Scanner in = null;
    private PrintWriter out = null;

    public RPCMessageProcess(RPCMessageHandler messageHandler, Socket socket) {
        this.messageHandler = messageHandler;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        }
        catch (IOException e) {
            logger.error("socket get scanner and printwriter error", e);
        }
        while (in.hasNextLine()) {
            String message = in.nextLine().trim();
            if ("exit".equals(message)) {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    logger.error("socket.close error", e);
                }
                break;
            }
            String response = messageHandler.handle(message);
            out.println(response.trim());
            out.flush();
        }
    }
}
