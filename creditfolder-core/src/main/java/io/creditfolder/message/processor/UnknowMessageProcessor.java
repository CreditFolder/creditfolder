package io.creditfolder.message.processor;

import io.creditfolder.message.command.Message;
import io.creditfolder.message.MessageProcessor;
import io.creditfolder.peer.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 19:12
 */
@Component
public class UnknowMessageProcessor extends MessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(UnknowMessageProcessor.class);
    @Override
    public void process(Message message, Peer peer) {
        logger.info("unknow message, do nothing");
    }
}
