package io.creditfolder.message;

import io.creditfolder.peer.Peer;
import org.codehaus.jettison.json.JSONObject;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 18:07
 */
public class Message {
    /* 发送消息的节点 */
    private Peer peer;
    /* 消息内容 */
    private JSONObject content;

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }
}
