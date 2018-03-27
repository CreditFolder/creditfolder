package io.creditfolder.config;

import io.creditfolder.peer.Seed;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:04
 */
public class MainNetworkConfig implements NetworkConfig {

    private static final List<Seed> seedList = new ArrayList<>();
    private static final int SERVER_PORT = 10086;
    private static final int MAX_IN_CONNECT = 3;
    private static final int MAX_OUT_CONNECT = 2;

    static {
        // creditfolder tokyo服务器地址
        seedList.add(new Seed(new InetSocketAddress("127.0.0.1", SERVER_PORT)));
    }

    public MainNetworkConfig() {
    }

    @Override
    public List<Seed> getAllSeed() {
        return seedList;
    }

    @Override
    public int getServerPort() {
        String port = System.getProperty("server.port");
        System.out.println("server.port=" + port);
        if (StringUtils.isEmpty(port)) {
            return SERVER_PORT;
        }
        return Integer.parseInt(port);
    }

    @Override
    public int getMaxOutConnect() {
        return MAX_OUT_CONNECT;
    }

    @Override
    public int getMaxInConnect() {
        return MAX_IN_CONNECT;
    }
}
