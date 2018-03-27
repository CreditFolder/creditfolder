package io.creditfolder.message;

import io.creditfolder.peer.Peer;
import io.creditfolder.peer.PeerKeeper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 02:10
 */
@Component
public class MessageHandler {
    @Autowired
    private PeerKeeper peerKeeper;

    public JSONObject handle(JSONObject message) throws JSONException {
        JSONObject response = new JSONObject();
        response.put("success", true);

        String command = message.getString("command");

        if ("help".equals(command)) {
            response.put("message", "command 'getallpeers' can get my all peer connected");
            return response;
        }
        // 获取所有连接的节点
        else if ("getallpeers".equals(command)) {
            List<Peer> peerList = peerKeeper.getAllConnect();
            List<JSONObject> peerInfoList = new ArrayList<>();

            for (Peer peer : peerList) {
                peerInfoList.add(peer.toJSONObject());
            }
            response.put("data", peerInfoList);
            return response;
        }
        else if ("newblock".equals(command)) {
            response.put("message", "sorry, not support");
            return response;
        }
        else {
            response.put("message", "I can't understand, type 'help' to get help");
            return response;
        }
    }
}
