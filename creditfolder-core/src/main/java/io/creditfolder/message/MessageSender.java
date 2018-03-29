package io.creditfolder.message;

import io.creditfolder.peer.Peer;
import io.creditfolder.peer.PeerKeeper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月28日 14:38
 */
@Component
public class MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    @Autowired
    private PeerKeeper peerKeeper;

    public void broadCastMoreSeeds() {
        JSONObject command = new JSONObject();
        try {
            command.put("command", "yourseeds");
            broadCast(command);
        }
        catch (JSONException e) {
            logger.error("build command error", e);
        }
    }

    /**
     * 广播
     * @param command
     */
    private void broadCast(JSONObject command) {
        for (Peer peer : peerKeeper.getAllPeers()) {
            try {
                peer.write(command);
            }
            catch (IOException e) {
                logger.error("broadcast command to peer {} error command {}", peer, command, e);
            }
        }
    }
}
