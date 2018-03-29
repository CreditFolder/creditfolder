package io.creditfolder.rpc;

import io.creditfolder.peer.*;
import io.creditfolder.seed.Seed;
import io.creditfolder.seed.SeedKeeper;
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
    @Autowired
    private SeedKeeper seedKeeper;

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
            case "seedinfo": {
                List<Seed> seedList = seedKeeper.getAllSeeds();
                if (CollectionUtils.isEmpty(seedList)) {
                    return "没有任何种子节点信息";
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (Seed seed : seedList) {
                    stringBuilder.append(seed.toString() + "\n");
                }
                return stringBuilder.toString();
            }
            case "alivecheck": {
                PeerAliveCountChecker checker = new PeerAliveCountChecker(peerKeeper, peerDiscovery);
                checker.run();
                return "alive check finished";
            }
            case "morepeer": {
                peerDiscovery.findMorePeers();
                return "morepeer has connected";
            }
            default:{
                return  "命令无法识别，如需帮助，请输入'help'";
            }
        }
    }
}