package io.creditfolder.message;

import io.creditfolder.message.command.*;
import io.creditfolder.message.processor.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 18:16
 */
public final class CommandDefinition {
    public static final Map<String, Class<? extends Message>> COMMAND_MESSAGE = new HashMap<>();
    public static final Map<Class<? extends Message>, String> MESSAGE_COMMAND = new HashMap<>();
    public static final Map<Class<? extends Message>, Class<? extends MessageProcessor>> MESSAGE_PROCESSOR = new HashMap<>();

    static {
        COMMAND_MESSAGE.put("version", VersionMessage.class);
        COMMAND_MESSAGE.put("verack", VerackMessage.class);
        COMMAND_MESSAGE.put("ping", PingMessage.class);
        COMMAND_MESSAGE.put("pong", PongMessage.class);
        COMMAND_MESSAGE.put("addr", AddressMessage.class);
        COMMAND_MESSAGE.put("getaddr", GetAddressMessage.class);

        MESSAGE_COMMAND.put(VersionMessage.class, "version");
        MESSAGE_COMMAND.put(VerackMessage.class, "verack");
        MESSAGE_COMMAND.put(PingMessage.class, "ping");
        MESSAGE_COMMAND.put(PongMessage.class, "pong");
        MESSAGE_COMMAND.put(AddressMessage.class, "addr");
        MESSAGE_COMMAND.put(GetAddressMessage.class, "getaddr");

        MESSAGE_PROCESSOR.put(VersionMessage.class, VersionMessageProcessor.class);
        MESSAGE_PROCESSOR.put(VerackMessage.class, VerackMessageProcessor.class);
        MESSAGE_PROCESSOR.put(PingMessage.class, PingMessageProcessor.class);
        MESSAGE_PROCESSOR.put(PongMessage.class, PongMessageProcessor.class);
        MESSAGE_PROCESSOR.put(AddressMessage.class, AddressMessageProcessor.class);
        MESSAGE_PROCESSOR.put(GetAddressMessage.class, GetAddressMessageProcessor.class);
    }
}
