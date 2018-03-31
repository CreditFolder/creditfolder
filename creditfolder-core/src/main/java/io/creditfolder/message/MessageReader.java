package io.creditfolder.message;

import io.creditfolder.message.command.Message;
import io.creditfolder.peer.PeerCloseException;
import io.creditfolder.utils.Utils;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息读取
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 12:10
 */
public class MessageReader {
    /* 专门用于读的buffer */
    public ByteBuffer buffer = ByteBuffer.allocateDirect(512);
    private byte[] largerBuffer;
    private MessageHeader header;
    private int largeBufferPos;

    public List<Message> read(SelectionKey key) throws IOException, PeerCloseException, ProtocolException {
        List<Message> messageList = new ArrayList<>();
        int count = ((SocketChannel)key.channel()).read(buffer);
        if (count == 0) {
            return messageList;
        }
        else if (count == -1) {
            throw new PeerCloseException();
        }
        buffer.flip();
        Utils.checkState(buffer.position() == 0 && buffer.capacity() >= MessageHeader.HEADER_LENGTH);
        try {
            boolean firstMessage = true;
            while (true) {
                if (largerBuffer != null) {
                    Utils.checkState(firstMessage);
                    int bytesToGet = Math.min(buffer.remaining(), largerBuffer.length - largeBufferPos);
                    buffer.get(largerBuffer, largeBufferPos, bytesToGet);
                    largeBufferPos += bytesToGet;
                    // we got all bytes
                    if (largeBufferPos == largerBuffer.length) {
                        // we got a new message
                        Message message = MessageSerializer.parseMessage(header, ByteBuffer.wrap(largerBuffer));
                        messageList.add(message);
                        largerBuffer = null;
                        header = null;
                        firstMessage = false;
                    }
                }

                int prePosition = buffer.position();
                try {
                    Message message = MessageSerializer.parseMessage(buffer);
                    messageList.add(message);
                }
                catch (BufferUnderflowException e) {
                    // 第一个消息就发现，readbuffer已经写满了，那么启用largeReadBuffer
                    if (firstMessage && buffer.limit() == buffer.capacity()) {
                        buffer.position(0);
                        try {
                            header = MessageSerializer.parseMessageHeader(buffer);
                            largerBuffer = new byte[header.size];
                            largeBufferPos = buffer.remaining();
                            buffer.get(largerBuffer, 0, largeBufferPos);
                        }
                        catch (BufferUnderflowException e1) {
                            throw new ProtocolException("No magic bytes+header after reading " + buffer.capacity() + " bytes");
                        }
                    }
                    // channel中积压了多个消息，缓冲区不够用，那么恢复，放到后面处理了。
                    else {
                        buffer.position(prePosition);
                    }
                    return messageList;
                }
                firstMessage = false;
            }
        }
        finally {
            buffer.compact();
        }
    }
}
