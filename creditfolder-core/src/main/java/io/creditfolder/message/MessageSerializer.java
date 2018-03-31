package io.creditfolder.message;

import com.alibaba.fastjson.JSONObject;
import io.creditfolder.message.command.Message;
import io.creditfolder.utils.Sha256Hash;
import io.creditfolder.utils.Utils;

import java.nio.ByteBuffer;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 17:14
 */
public class MessageSerializer {
    public static final int COMMAND_LE = 12;

    public static Message parseMessage(ByteBuffer buffer) throws ProtocolException {
        MessageHeader header = parseMessageHeader(buffer);
        return parseMessage(header, buffer);
    }

    public static Message parseMessage(MessageHeader header, ByteBuffer buffer) {
        byte[] messageBytes = new byte[header.size];
        buffer.get(messageBytes, 0, header.size);

        Class<? extends Message> clazz = CommandDefinition.COMMAND_MESSAGE.get(header.command);
        return JSONObject.parseObject(messageBytes, clazz);
    }

    public static MessageHeader parseMessageHeader(ByteBuffer buffer) throws ProtocolException {
        return new MessageHeader(buffer);
    }

    public static byte[] serialize(Message message) {
        String command = CommandDefinition.MESSAGE_COMMAND.get(message.getClass());
        byte[] contentBytes = JSONObject.toJSONBytes(message);
        byte[] result = new byte[COMMAND_LE + 4 + 4 + contentBytes.length];
        for (int i = 0; i < command.length() && i < COMMAND_LE; i++) {
            result[i] = (byte)(command.codePointAt(i) & 0xFF);
        }

        Utils.uint32ToByteArrayLE(contentBytes.length, result, COMMAND_LE);
        byte[] hash = Sha256Hash.hashTwice(contentBytes);
        System.arraycopy(hash, 0, result, COMMAND_LE +4, 4);
        System.arraycopy(contentBytes, 0, result, COMMAND_LE + 4 + 4, contentBytes.length);
        return result;
    }
}