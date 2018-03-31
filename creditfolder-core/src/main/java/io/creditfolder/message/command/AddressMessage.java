package io.creditfolder.message.command;

import io.creditfolder.seed.Seed;

import java.util.ArrayList;
import java.util.List;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 19:21
 */
public class AddressMessage extends Message {
    private List<Seed> seedList = new ArrayList<>();

    public AddressMessage() {
    }

    public AddressMessage(List<Seed> seedList) {
        this.seedList = seedList;
    }

    public List<Seed> getSeedList() {
        return seedList;
    }

    public void setSeedList(List<Seed> seedList) {
        this.seedList = seedList;
    }

    @Override
    public String toString() {
        return "AddressMessage{" +
                "seedList=" + seedList +
                '}';
    }
}
