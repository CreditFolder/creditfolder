package io.creditfolder.message;

import io.creditfolder.peer.Peer;
import io.creditfolder.peer.PeerKeeper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 02:26
 */
public class MessageProcess implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MessageProcess.class);
    private MessageHandler messageHandler;
    private Peer peer = null;
    private PeerKeeper peerKeeper;

    public MessageProcess(Peer peer, MessageHandler messageHandler, PeerKeeper peerKeeper) {
        this.peer = peer;
        this.messageHandler = messageHandler;
        this.peerKeeper = peerKeeper;
    }

    @Override
    public void run() {
        try {
            while (peer.hasMessage()) {
                JSONObject message = peer.read();
                if ("exit".equals(message.getString("command"))) {
                    break;
                }
                JSONObject response = messageHandler.handle(message);
                peer.write(response);
            }
        }
        catch (IOException e) {
            logger.error("MessageProcess error", e);
        }
        catch (JSONException e) {
            try {
                JSONObject response = new JSONObject();
                response.put("success", "false");
                response.put("message", "格式错误");
                peer.write(response);
            }
            catch (Exception e1) {
                logger.error("message loop error", e1);
            }
        }
        finally {
            try {
                peer.close();
                peerKeeper.removePeer(peer);
            }
            catch (IOException e) {
                logger.error("peer.close error", e);
            }
        }
    }
}