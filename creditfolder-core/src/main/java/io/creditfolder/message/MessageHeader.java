package io.creditfolder.message;

import io.creditfolder.message.command.Message;
import io.creditfolder.utils.Utils;

import java.nio.ByteBuffer;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 12:24
 */
public class MessageHeader {
    /* The largest number of bytes that a header can represent */
    public static final int COMMAND_LEN = 12;
    public static final int HEADER_LENGTH = COMMAND_LEN + 4 + 4;

    public final byte[] header;
    public final String command;
    public final int size;
    public final byte[] checksum;

    public MessageHeader(ByteBuffer buffer) throws ProtocolException {
        header = new byte[HEADER_LENGTH];
        buffer.get(header, 0, header.length);

        int cursor = 0;

        for (; header[cursor] != 0 && cursor < COMMAND_LEN; cursor++);
        byte[] commandBytes = new byte[cursor];
        System.arraycopy(header, 0, commandBytes, 0, cursor);;
        command = Utils.toString(commandBytes, "US-ASCII");
        cursor = COMMAND_LEN;

        size = (int)Utils.readUint32(header, cursor);
        cursor += 4;

        if (size > Message.MAX_SIZE || size < 0) {
            throw new ProtocolException("Message size too large:" + size);
        }

        checksum = new byte[4];
        System.arraycopy(header, cursor, checksum, 0, 4);
    }
}
