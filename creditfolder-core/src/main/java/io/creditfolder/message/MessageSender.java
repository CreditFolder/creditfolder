package io.creditfolder.message;

import io.creditfolder.message.command.*;
import io.creditfolder.peer.Peer;
import io.creditfolder.peer.PeerKeeper;
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

    public void ping(Peer peer) {
        peer.write(new PingMessage());
    }

    public void pong(Peer peer) {
        peer.write(new PongMessage());
    }

    public void address(Peer peer, AddressMessage message) {
        peer.write(message);
    }

    public void broadGetAddress() {
        GetAddressMessage message = new GetAddressMessage();
        broadCast(message);
    }

    /**
     * 广播
     * @param message
     */
    private void broadCast(Message message) {
        for (Peer peer : peerKeeper.getAllPeers()) {
            peer.write(message);
        }
    }
}
