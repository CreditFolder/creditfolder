package io.creditfolder.seed;

import io.creditfolder.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static ArrayBlockingQueue<Seed> newSeedsQueue = new ArrayBlockingQueue<>(1024);
    private Seed seedMyself;

    private CopyOnWriteArraySet<Seed> currentSeedList = new CopyOnWriteArraySet<>();

    public void init() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            seedMyself = new Seed(ip, Config.SERVER_PORT, Config.MY_SEED_NAME);
        }
        catch (UnknownHostException e) {
            logger.error("build myselfseed error");
        }
    }

    /**
     * 这个节点是否已经存在
     * @param seed
     * @return
     */
    public boolean hasExist(Seed seed) {
        return currentSeedList.contains(seed);
    }

    public void saveSeed(Seed seed) {
        logger.info("find new seed {}", seed);
        currentSeedList.add(seed);
    }

    public void saveAllSeeds(Collection<Seed> newSeeds) {
        for (Seed newSeed : newSeeds) {
            logger.info("find new seed {}", newSeed);
            currentSeedList.add(newSeed);
        }
    }

    public List<Seed> getAllSeeds() {
        return new ArrayList<>(currentSeedList);
    }

    public boolean isMyself(Seed seed) {
        return seed.equals(seedMyself);
    }

    public Seed getSeedMyself() {
        return seedMyself;
    }
}
