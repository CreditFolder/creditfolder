package io.creditfolder.peer;

import io.creditfolder.config.Config;
import io.creditfolder.message.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Nodes discover their own external address by various methods.
 * Nodes read the callback address of remote nodes that connect to them.
 * Nodes makes DNS request to read IP addresses.
 * Nodes can use addresses hard coded into the software.
 * Nodes exchange addresses with other nodes.
 * Nodes store addresses in a database and read that database on startup.
 * Nodes can be provided addresses as command line arguments
 * Nodes read addresses from a user provided text file on startup
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月27日 19:03
 */
@Component
public class PeerDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(PeerDiscovery.class);

    @Autowired
    private PeerKeeper peerKeeper;
    @Autowired
    private MessageSender messageSender;

    /**
     * 主动连接更多的节点
     */
    public void findMorePeers() {
        if (needMoreOutPeers()) {
            messageSender.broadGetAddress();
        }
    }

    /**
     * 是否需要更多的节点
     * @return
     */
    public boolean needMoreOutPeers() {
        return peerKeeper.getOutConnectCount() < Config.MIN_OUT_CONNECT;
    }
}
