package io.creditfolder.message.processor;

import io.creditfolder.message.command.Message;
import io.creditfolder.message.MessageProcessor;
import io.creditfolder.message.command.PongMessage;
import io.creditfolder.peer.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 19:24
 */
@Component
public class PongMessageProcessor extends MessageProcessor<PongMessage> {
    private static final Logger logger = LoggerFactory.getLogger(PongMessageProcessor.class);

    @Override
    public void process(PongMessage message, Peer peer) {
        logger.info("receive a pong message from {}", peer);
    }
}
