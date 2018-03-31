package io.creditfolder.message.processor;

import io.creditfolder.message.command.Message;
import io.creditfolder.message.MessageProcessor;
import io.creditfolder.message.command.VerackMessage;
import io.creditfolder.peer.Peer;
import org.springframework.stereotype.Component;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 18:59
 */
@Component
public class VerackMessageProcessor extends MessageProcessor<VerackMessage> {

    @Override
    public void process(VerackMessage message, Peer peer) {
        super.process(message, peer);
    }
}
