package io.creditfolder.config;

import io.creditfolder.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:04
 */
public class MainNetworkConfig implements NetworkConfig {
    private static final Logger logger = LoggerFactory.getLogger(MainNetworkConfig.class);

    private static final List<Seed> seedList = new ArrayList<>();
    // 钱包服务器端口
    private static final int SERVER_PORT_DEFAULT = 10086;
    // 当被动连接数大于等于这个数的时候，关闭被动连接端口
    private static final int MAX_IN_CONNECT_DEFAULT = 3;
    // 当被动连接数小于这个数的时候，打开被动连接端口
    private static final int MIN_IN_CONNECT_DEFAULT = 1;
    // 当主动连接大于这个数的时候，不在主动连接其他节点
    private static final int MAX_OUT_CONNECT_DEFAULT = 3;
    // 当主动连接小于这个数的时候，主动连接其他节点
    private static final int MIN_OUT_CONNECT_DEFAULT = 2;
    private static final int RPC_SERVER_PORT_DEFAULT = 1111;
    private static final boolean ISSEED_DEFAULT = false;

    private static final String KEY_SERVER_PORT = "server.port";
    private static final String KEY_MAX_IN_CONNECT = "max.in.connect";
    private static final String KEY_MIN_IN_CONNECT = "min.in.connect";
    private static final String KEY_MAX_OUT_CONNECT = "max.out.connect";
    private static final String KEY_MIN_OUT_CONNECT = "min.out.connect";
    private static final String KEY_RPC_SERVER_PORT = "rpc.server.port";
    private static final String KEY_ISSEED = "isseed";
    private static final String KEY_MY_SEED_NAME = "my.seed.name";

    static {
        // creditfolder tokyo服务器地址
        // seedList.add(new Seed(new InetSocketAddress("13.231.146.23", SERVER_PORT_DEFAULT)));
        seedList.add(new Seed("192.168.1.100", SERVER_PORT_DEFAULT, "test.creditfolder.io"));
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
        if (StringUtils.isEmpty(port)) {
            return SERVER_PORT_DEFAULT;
        }
        return Integer.parseInt(port);
    }

    @Override
    public int getMaxOutConnect() {
        String maxOutConnect = System.getProperty(KEY_MAX_OUT_CONNECT);
        if (StringUtils.isEmpty(maxOutConnect)) {
            return MAX_OUT_CONNECT_DEFAULT;
        }
        return Integer.parseInt(maxOutConnect);
    }

    @Override
    public int getMinOutConnect() {
        String minOutConnect = System.getProperty(KEY_MIN_OUT_CONNECT);
        if (StringUtils.isEmpty(minOutConnect)) {
            return MIN_OUT_CONNECT_DEFAULT;
        }
        return Integer.parseInt(minOutConnect);
    }

    @Override
    public int getMaxInConnect() {
        String maxInConnect = System.getProperty(KEY_MAX_IN_CONNECT);
        if (StringUtils.isEmpty(maxInConnect)) {
            return MAX_IN_CONNECT_DEFAULT;
        }
        return Integer.parseInt(maxInConnect);
    }

    @Override
    public int getMinInConnect() {
        String minInConnect = System.getProperty(KEY_MIN_IN_CONNECT);
        if (StringUtils.isEmpty(minInConnect)) {
            return MAX_IN_CONNECT_DEFAULT;
        }
        return Integer.parseInt(minInConnect);
    }

    @Override
    public int getRPCServerPort() {
        String rpcServerPort = System.getProperty(KEY_RPC_SERVER_PORT);
        if (StringUtils.isEmpty(rpcServerPort)) {
            return RPC_SERVER_PORT_DEFAULT;
        }
        return Integer.parseInt(rpcServerPort);
    }

    @Override
    public boolean isSeed() {
        String isSeed = System.getProperty(KEY_ISSEED);
        if (StringUtils.isEmpty(isSeed)) {
            return ISSEED_DEFAULT;
        }
        return Boolean.parseBoolean(isSeed);
    }

    @Override
    public String getMySeedName() {
        String mySeedName = System.getProperty(KEY_MY_SEED_NAME);
        if (!StringUtils.isEmpty(mySeedName)) {
            return mySeedName;
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            return "anonymous";
        }
    }

    @Override
    public void showInfo() {
        logger.info("* CreditFolder Wallet Start Info *");
        logger.info("* " + KEY_SERVER_PORT + "=" + getServerPort());
        logger.info("* " + KEY_MAX_IN_CONNECT + "=" + getMaxInConnect());
        logger.info("* " + KEY_MIN_IN_CONNECT + "=" + getMinInConnect());
        logger.info("* " + KEY_MAX_OUT_CONNECT + "=" + getMaxOutConnect());
        logger.info("* " + KEY_MIN_OUT_CONNECT + "=" + getMinOutConnect());
        logger.info("* " + KEY_ISSEED + "=" + isSeed());
        logger.info("* " + KEY_RPC_SERVER_PORT + "=" + getRPCServerPort());
        logger.info("* " + KEY_MY_SEED_NAME + "=" + getMySeedName());
    }
}
