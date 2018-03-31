package io.creditfolder.message;

import io.creditfolder.message.command.Message;
import io.creditfolder.peer.Peer;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 18:59
 */
public abstract class MessageProcessor<T extends Message> {

    public void process(T message, Peer peer) {

    };
}
