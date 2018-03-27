package io.creditfolder.peer;

import java.net.InetSocketAddress;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:00
 */
public class Seed {
    /* IP + 端口 */
    private InetSocketAddress address;
    /* 每个节点的名称，完全是为了好记 */
    private String name;

    public Seed(InetSocketAddress address) {
        this.address = address;
    }

    public Seed(InetSocketAddress address, String name) {
        this.address = address;
        this.name = name;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Seed{" +
                "address=" + address +
                ", name='" + name + '\'' +
                '}';
    }
}
