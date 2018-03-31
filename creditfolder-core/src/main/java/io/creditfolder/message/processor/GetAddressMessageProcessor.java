package io.creditfolder.message.processor;

import io.creditfolder.message.command.AddressMessage;
import io.creditfolder.message.command.GetAddressMessage;
import io.creditfolder.message.command.Message;
import io.creditfolder.message.MessageProcessor;
import io.creditfolder.peer.Peer;
import io.creditfolder.seed.Seed;
import io.creditfolder.seed.SeedKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 19:26
 */
@Component
public class GetAddressMessageProcessor extends MessageProcessor<GetAddressMessage> {
    @Autowired
    private SeedKeeper seedKeeper;

    @Override
    public void process(GetAddressMessage message, Peer peer) {
        List<Seed> seedList = seedKeeper.getAllSeeds();
        seedList.add(seedKeeper.getSeedMyself());
        AddressMessage addressMessage = new AddressMessage();
        addressMessage.setSeedList(seedList);
        peer.write(addressMessage);
    }
}
