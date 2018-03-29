package io.creditfolder.message;

import io.creditfolder.peer.Peer;
import org.codehaus.jettison.json.JSONObject;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 18:07
 */
public class PeerMessage {
    /* 发送消息的节点 */
    private Peer sender;
    /* 消息内容 */
    private JSONObject content;

    public Peer getSender() {
        return sender;
    }

    public void setSender(Peer sender) {
        this.sender = sender;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public static PeerMessage build(JSONObject content, Peer sender) {
        PeerMessage message = new PeerMessage();
        message.sender = sender;
        message.content = content;
        return message;
    }
}
