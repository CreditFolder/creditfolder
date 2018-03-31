package io.creditfolder.message.processor;

import io.creditfolder.message.command.AddressMessage;
import io.creditfolder.message.command.Message;
import io.creditfolder.message.MessageProcessor;
import io.creditfolder.peer.Peer;
import io.creditfolder.peer.PeerKeeper;
import io.creditfolder.seed.Seed;
import io.creditfolder.seed.SeedKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.logging.Logger;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 19:24
 */
@Component
public class AddressMessageProcessor extends MessageProcessor<AddressMessage> {

    @Autowired
    private SeedKeeper seedKeeper;

    @Override
    public void process(AddressMessage message, Peer peer) {
        List<Seed> seedList = message.getSeedList();
        if (CollectionUtils.isEmpty(seedList)) {
            return;
        }
        for (Seed seed : seedList) {
            if (!seedKeeper.newSeedsQueue.offer(seed)) {
                return;
            }
        }
    }
}
