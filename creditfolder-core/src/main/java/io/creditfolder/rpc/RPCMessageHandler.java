package io.creditfolder.rpc;

import io.creditfolder.peer.Peer;
import io.creditfolder.peer.PeerAliveCountChecker;
import io.creditfolder.peer.PeerDiscovery;
import io.creditfolder.peer.PeerKeeper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月27日 16:45
 */
@Component
public class RPCMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(RPCMessageHandler.class);
    @Autowired
    private PeerKeeper peerKeeper;
    @Autowired
    private PeerDiscovery peerDiscovery;

    public String handle(String message) {
        if (StringUtils.isEmpty(message)) {
            return "";
        }

        switch (message) {
            case "help": {
                return "command: 'getallpeers' can get all connect info";
            }
            case "peerinfo": {
                StringBuilder sb = new StringBuilder();
                List<Peer> inPeerList = peerKeeper.getInConnectList();
                if (CollectionUtils.isEmpty(inPeerList)) {
                    sb.append("被动连接节点数量：0\n");
                }
                else {
                    sb.append("被动连接节点数量：" + inPeerList.size() + "\n");
                    for (Peer peer : inPeerList) {
                        sb.append(peer.toString() + "\n");
                    }
                }
                List<Peer> outPeerList = peerKeeper.getOutConnectList();
                if (CollectionUtils.isEmpty(outPeerList)) {
                    sb.append("主动连接节点数量：0\n");
                }
                else {
                    sb.append("主动连接节点数量:" + outPeerList.size() + "\n");
                    for (Peer peer : outPeerList) {
                        sb.append(peer.toString() + "\n");
                    }
                }
                return sb.toString();
            }
            case "alivecheck": {
                PeerAliveCountChecker checker = new PeerAliveCountChecker(peerKeeper, peerDiscovery);
                checker.run();
                return "alive check finished";
            }
            case "morepeer": {
                peerDiscovery.connectMorePeer();
                return "morepeer has connected";
            }
            case "getallpeers": {
                List<Peer> peerList = peerKeeper.getAllConnect();
                JSONArray jsonArray = new JSONArray();
                List<JSONObject> peerJsonList = new ArrayList<>();
                try {
                    for (Peer peer : peerList) {
                        peerJsonList.add(peer.toJSONObject());
                    }
                }
                catch (JSONException e) {
                    logger.error("peer.toJSONObject() error", e);
                    return "system error";
                }
                jsonArray.put(peerJsonList);
                return jsonArray.toString();
            }
            default:{
                return  "命令无法识别，如需帮助，请输入'help'";
            }
        }
    }
}