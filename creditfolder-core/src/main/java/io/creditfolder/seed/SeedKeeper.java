package io.creditfolder.seed;

import io.creditfolder.config.NetworkConfig;
import io.creditfolder.peer.PeerKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 这个类只保存当前连接成功的节点，确保Seed是可用的
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月28日 22:25
 */
@Component
public class SeedKeeper {
    private static final Logger logger = LoggerFactory.getLogger(SeedKeeper.class);
    // 用于存放新发现的节点，等待连接
    public static ArrayBlockingQueue<Seed> seedsQueue = new ArrayBlockingQueue<>(20);
    private Seed seedMyself;
    @Autowired
    private NetworkConfig networkConfig;
    @Autowired
    private PeerKeeper peerKeeper;


    private CopyOnWriteArraySet<Seed> seeds = new CopyOnWriteArraySet<>();

    public void init() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            seedMyself = new Seed(ip, networkConfig.getServerPort(), networkConfig.getMySeedName());
        }
        catch (UnknownHostException e) {
            logger.error("build myselfseed error");
        }
        Thread thread = new Thread(new SeedQueueConsumer(peerKeeper));
        thread.setName("seedQueueConsumer");
        thread.start();
    }

    /**
     * 这个节点是否已经存在
     * @param seed
     * @return
     */
    public boolean hasExist(Seed seed) {
        return seeds.contains(seed);
    }

    public void saveSeed(Seed seed) {
        logger.info("find new seed {}", seed);
        seeds.add(seed);
    }

    public void saveAllSeeds(Collection<Seed> newSeeds) {
        for (Seed newSeed : newSeeds) {
            logger.info("find new seed {}", newSeed);
            seeds.add(newSeed);
        }
    }

    public List<Seed> getAllSeeds() {
        return new ArrayList<>(seeds);
    }

    public boolean isMyself(Seed seed) {
        return seed.equals(seedMyself);
    }

    public Seed getSeedMyself() {
        return seedMyself;
    }
}
