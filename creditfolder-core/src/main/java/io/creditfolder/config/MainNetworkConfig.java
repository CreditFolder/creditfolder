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
    private static final int SERVER_PORT_DEFAULT = 10086;
    private static final int MAX_IN_CONNECT_DEFAULT = 3;
    private static final int MAX_OUT_CONNECT_DEFAULT = 2;
    private static final int RPC_SERVER_PORT_DEFAULT = 1111;
    private static final boolean ISSEED_DEFAULT = false;

    private static final String KEY_SERVER_PORT = "server.port";
    private static final String KEY_MAX_IN_CONNECT = "max.in.connect";
    private static final String KEY_MAX_OUT_CONNECT = "max.out.connect";
    private static final String KEY_RPC_SERVER_PORT = "rpc.server.port";
    private static final String KEY_ISSEED = "isseed";

    static {
        // creditfolder tokyo服务器地址
        seedList.add(new Seed(new InetSocketAddress("13.231.146.23", SERVER_PORT_DEFAULT)));
    }

    public MainNetworkConfig() {
    }

    @Override
    public List<Seed> getAllSeed() {
        return seedList;
    }

    @Override
    public int getServerPort() {
        String port = System.getProperty(KEY_SERVER_PORT);
        System.out.println(KEY_SERVER_PORT + "=" + port);
        if (StringUtils.isEmpty(port)) {
            return SERVER_PORT_DEFAULT;
        }
        return Integer.parseInt(port);
    }

    @Override
    public int getMaxOutConnect() {
        String maxOutConnect = System.getProperty(KEY_MAX_OUT_CONNECT);
        System.out.println(KEY_MAX_OUT_CONNECT + "=" + maxOutConnect);
        if (StringUtils.isEmpty(maxOutConnect)) {
            return MAX_OUT_CONNECT_DEFAULT;
        }
        return Integer.parseInt(maxOutConnect);
    }

    @Override
    public int getMaxInConnect() {
        String maxInConnect = System.getProperty(KEY_MAX_IN_CONNECT);
        System.out.println(KEY_MAX_IN_CONNECT + "=" + maxInConnect);
        if (StringUtils.isEmpty(maxInConnect)) {
            return MAX_IN_CONNECT_DEFAULT;
        }
        return Integer.parseInt(maxInConnect);
    }

    @Override
    public int getRPCServerPort() {
        String rpcServerPort = System.getProperty(KEY_RPC_SERVER_PORT);
        System.out.println(KEY_RPC_SERVER_PORT + "=" + rpcServerPort);
        if (StringUtils.isEmpty(rpcServerPort)) {
            return RPC_SERVER_PORT_DEFAULT;
        }
        return Integer.parseInt(rpcServerPort);
    }

    @Override
    public boolean isSeed() {
        String isSeed = System.getProperty(KEY_ISSEED);
        System.out.println(KEY_ISSEED + "=" + isSeed);
        if (StringUtils.isEmpty(isSeed)) {
            return ISSEED_DEFAULT;
        }
        return Boolean.parseBoolean(isSeed);
    }
}
