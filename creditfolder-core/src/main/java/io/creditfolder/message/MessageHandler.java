package io.creditfolder.message;

import io.creditfolder.peer.Peer;
import io.creditfolder.peer.PeerDiscovery;
import io.creditfolder.peer.PeerKeeper;
import io.creditfolder.seed.Seed;
import io.creditfolder.seed.SeedKeeper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

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
    @Autowired
    private SeedKeeper seedKeeper;
    @Autowired
    private PeerDiscovery peerDiscovery;

    public void handle(PeerMessage message) throws JSONException, IOException {
        Peer sender = message.getSender();
        JSONObject content = message.getContent();
        String command = content.optString("command");

        switch (command) {
            case "help": {
                JSONObject response = new JSONObject();
                response.put("message", "command 'getallpeers' can get my all peer connected");
                sender.write(response);
                return;
            }
            case "ping": {
                JSONObject response = new JSONObject();
                response.put("message", "connection alive");
                sender.write(response);
                return;
            }
            // 对方请求我要更多的节点
            case "yourseeds": {
                List<Seed> seedList = seedKeeper.getAllSeeds();
                seedList.add(seedKeeper.getSeedMyself());
                List<JSONObject> seedInfoList = new ArrayList<>();
                for (Seed seed : seedList) {
                    seedInfoList.add(seed.toJSONObject());
                }
                JSONObject response = new JSONObject();
                response.put("command", "myseeds");
                response.put("data", seedInfoList);
                sender.write(response);
                return;
            }
            // 告诉我当前连接的节点，让本节点建立更多的连接
            // 获取到节点后，将节点信息加到阻塞队列中去，不要直接操作
            case "myseeds": {
                // 如果当前连接数量符合标准，则直接忽略这个消息
                if (!peerDiscovery.needMoreOutPeers()) {
                    return;
                }
                if (!content.has("data")) {
                    return;
                }
                JSONArray jsonArray = content.optJSONArray("data");
                List<Seed> seedList = new ArrayList<>();
                int count = 0;
                while (count < jsonArray.length()) {
                    JSONObject seedJson = jsonArray.getJSONObject(count);
                    Seed seed = Seed.parse(seedJson);
                    seedList.add(seed);
                    count++;
                }
                List<Seed> newSeedList = findNewSeeds(seedList);
                for (Seed seed : newSeedList) {
                    if (!SeedKeeper.seedsQueue.offer(seed)) {
                        return;
                    }
                }
                return;
            }
            // 对方想要获取我的连接地址
            case "youraddress": {
                JSONObject response = new JSONObject();
                response.put("command", "myaddress");
                response.put("data", seedKeeper.getSeedMyself().toJSONObject());
                sender.write(response);
                return;
            }
            case "myaddress": {
                JSONObject jsonObject = content.optJSONObject("data");
                Seed seed = Seed.parse(jsonObject);
                return;
            }
            case "newblock": {
                JSONObject response = new JSONObject();
                response.put("message", "sorry, not support");
                sender.write(response);
                return;
            }
            default: {
                return;
            }
        }
    }

    private List<Seed> findNewSeeds(List<Seed> seedList) {
        List<Seed> result = new ArrayList<>();
        for (Seed seed : seedList) {
            if (seedKeeper.hasExist(seed) || seedKeeper.isMyself(seed)) {
                continue;
            }
            else {
                result.add(seed);
            }
        }
        return result;
    }
}
