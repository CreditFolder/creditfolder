package io.creditfolder.message.processor;

import io.creditfolder.message.MessageSender;
import io.creditfolder.message.command.Message;
import io.creditfolder.message.MessageProcessor;
import io.creditfolder.message.command.PingMessage;
import io.creditfolder.peer.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 19:24
 */
@Component
public class PingMessageProcessor extends MessageProcessor<PingMessage> {
    private static final Logger logger = LoggerFactory.getLogger(PingMessageProcessor.class);
    @Autowired
    private MessageSender sender;

    @Override
    public void process(PingMessage message, Peer peer) {
        logger.info("receive a ping message from {}");
        sender.pong(peer);
    }
}
