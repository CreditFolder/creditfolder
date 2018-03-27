package io.creditfolder.peer;

import io.creditfolder.config.NetworkConfig;
import io.creditfolder.message.MessageHandler;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负责寻找更多节点
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月27日 19:03
 */
@Component
public class PeerDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(PeerDiscovery.class);
    // 防止有并发问题
    private volatile boolean isRunning = false;
    @Autowired
    private NetworkConfig networkConfig;
    @Autowired
    private PeerKeeper peerKeeper;
    @Autowired
    private MessageHandler messageHandler;
    // 等待连接的种子数量
    private AtomicInteger waitForConnect = new AtomicInteger(0);

    /**
     * 主动连接更多的节点
     */
    public void connectMorePeer() {
        if (isRunning || networkConfig.getMinOutConnect() <= peerKeeper.getOutConnectList().size()) {
            return;
        }
        isRunning = true;
        int maxCount = networkConfig.getMaxOutConnect() - peerKeeper.getOutConnectList().size();
        List<Seed> seedList = discoverySeed(maxCount);
//        waitForConnect.set(seedList.size());
        // 先同步实现，到后面换成异步通知的方式
        for (Seed seed : seedList) {
            SeedConnect connect = new SeedConnect(seed, peerKeeper, messageHandler);
            connect.run();
        }
        isRunning = false;
    }

    /**
     * 发现更多没有连接的节点
     * @param maxCount
     * @return
     */
    public List<Seed> discoverySeed(int maxCount) {
        logger.info("start to find more {} seed", maxCount);
        List<Peer> oldPeerList = peerKeeper.getAllConnect();
        List<Seed> seedList = new ArrayList<>();
        for (Peer oldPeer : oldPeerList) {
            List<Seed> findSeedList = findMorePeer(oldPeer);
            for (Seed seed : findSeedList) {
                if (oldPeerList.contains(seed) || seedList.contains(seed)) {
                    continue;
                }
                seedList.add(seed);
                if (seedList.size() >= maxCount) {
                    return seedList;
                }
            }
        }
        logger.info("find new {} seed:", seedList.size());
        for (Seed seed : seedList) {
            logger.info("{}", seed);
        }
        return seedList;
    }

    private List<Seed> findMorePeer(Peer peer) {
        try {
            JSONObject message = new JSONObject();
            message.put("command", "getallpeers");
            peer.request(message);
            JSONObject jsonObject = peer.getResponse();
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            List<Seed> newSeedList = new ArrayList<>();
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject peerJson = jsonArray.getJSONObject(count);
                Seed newSeed = Seed.parse(peerJson);
                newSeedList.add(newSeed);
                count++;
            }
            return newSeedList;
        }
        catch (JSONException e) {
            logger.error("findMorePeer failed");
            return Collections.emptyList();
        }
        catch (IOException e) {
            logger.error("findMorePeer failed");
            return Collections.emptyList();
        }
    }
}
