package io.creditfolder.message;

import io.creditfolder.message.command.Message;
import io.creditfolder.message.processor.MessageProcessorFactory;
import io.creditfolder.peer.Peer;
import io.creditfolder.peer.PeerDiscovery;
import io.creditfolder.peer.PeerKeeper;
import io.creditfolder.seed.SeedKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 02:10
 */
@Component
public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    @Autowired
    private MessageProcessorFactory processorFactory;

    public void handle(Message message, Peer sender) {
        MessageProcessor processor = processorFactory.getMessageProcessor(message);
        processor.process(message, sender);
    }
}
