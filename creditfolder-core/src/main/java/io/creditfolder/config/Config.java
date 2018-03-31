package io.creditfolder.config;

import io.creditfolder.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:45
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static final String NETWORK = System.getProperty("network", "main");

    public static NetworkConfig networkConfig = null;

    public static final long MAGICAL_NUMBER = 62941567738L;

    static {
        if (NETWORK == "main") {
            networkConfig = new MainNetworkConfig();
        }
    }

    // 钱包服务器端口
    public static final int SERVER_PORT = networkConfig.getServerPort();
    // 当被动连接数大于等于这个数的时候，关闭被动连接端口
    public static final int MAX_IN_CONNECT = networkConfig.getMaxInConnect();
    // 当被动连接数小于这个数的时候，打开被动连接端口
    public static final int MIN_IN_CONNECT = networkConfig.getMinInConnect();
    // 当主动连接大于这个数的时候，不在主动连接其他节点
    public static final int MAX_OUT_CONNECT = networkConfig.getMaxOutConnect();
    // 当主动连接小于这个数的时候，主动连接其他节点
    public static final int MIN_OUT_CONNECT = networkConfig.getMinOutConnect();
    public static final int RPC_SERVER_PORT = networkConfig.getRPCServerPort();
    public static final boolean ISGENESIS = networkConfig.isGenesisSeed();
    public static final List<Seed> SUPERSEEDLIST = networkConfig.getAllSuperSeed();
    public static final String MY_SEED_NAME = networkConfig.getMySeedName();

    public static void showInfo() {
        logger.info("* CreditFolder Wallet Start Info *");
        logger.info("* " + NetworkConfig.KEY_SERVER_PORT + "=" + SERVER_PORT);
        logger.info("* " + NetworkConfig.KEY_MAX_IN_CONNECT + "=" + MAX_IN_CONNECT);
        logger.info("* " + NetworkConfig.KEY_MIN_IN_CONNECT + "=" + MIN_IN_CONNECT);
        logger.info("* " + NetworkConfig.KEY_MAX_OUT_CONNECT + "=" + MAX_OUT_CONNECT);
        logger.info("* " + NetworkConfig.KEY_MIN_OUT_CONNECT + "=" + MIN_OUT_CONNECT);
        logger.info("* " + NetworkConfig.KEY_ISGENESIS + "=" + ISGENESIS);
        logger.info("* " + NetworkConfig.KEY_RPC_SERVER_PORT + "=" + RPC_SERVER_PORT);
        logger.info("* " + NetworkConfig.KEY_MY_SEED_NAME + "=" + MY_SEED_NAME);
    }
}
