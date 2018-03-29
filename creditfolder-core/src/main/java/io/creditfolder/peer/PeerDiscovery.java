package io.creditfolder.peer;

import io.creditfolder.config.NetworkConfig;
import io.creditfolder.message.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 负责寻找更多节点
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月27日 19:03
 */
@Component
public class PeerDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(PeerDiscovery.class);

    @Autowired
    private NetworkConfig networkConfig;
    @Autowired
    private PeerKeeper peerKeeper;
    @Autowired
    private MessageSender messageSender;

    /**
     * 主动连接更多的节点
     */
    public void findMorePeers() {
        if (needMoreOutPeers()) {
            messageSender.broadCastMoreSeeds();
        }
    }

    /**
     * 是否需要更多的节点
     * @return
     */
    public boolean needMoreOutPeers() {
        return peerKeeper.getOutConnectCount() < networkConfig.getMinOutConnect();
    }
}
