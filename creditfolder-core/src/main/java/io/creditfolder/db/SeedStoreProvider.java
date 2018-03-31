package io.creditfolder.db;

import io.creditfolder.seed.Seed;
import io.creditfolder.seed.SeedKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月30日 20:31
 */
@Component
public class SeedStoreProvider {
    private CopyOnWriteArraySet<Seed> allSeedList = new CopyOnWriteArraySet<>();
    @Autowired
    private SeedKeeper seedKeeper;

    /**
     * save seed into db
     * @param seed
     */
    public boolean store(Seed seed) {
        if (seed.equals(seedKeeper.getSeedMyself())) {
            return false;
        }
        return allSeedList.add(seed);
    }

    /**
     * 获取所有的Seed
     * @return
     */
    public CopyOnWriteArraySet<Seed> getAllSeedList() {
        return allSeedList;
    }
}
