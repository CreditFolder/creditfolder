package io.creditfolder.config;

import io.creditfolder.seed.Seed;

import java.util.List;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:06
 */
public interface NetworkConfig {

    String KEY_SERVER_PORT = "server.port";
    String KEY_MAX_IN_CONNECT = "max.in.connect";
    String KEY_MIN_IN_CONNECT = "min.in.connect";
    String KEY_MAX_OUT_CONNECT = "max.out.connect";
    String KEY_MIN_OUT_CONNECT = "min.out.connect";
    String KEY_RPC_SERVER_PORT = "rpc.server.port";
    String KEY_ISGENESIS = "isgenesis";
    String KEY_MY_SEED_NAME = "my.seed.name";

    /**
     * 获取所有的种子
     * @return
     */
    List<Seed> getAllSuperSeed();

    /**
     * 获取钱包端口
     * @return
     */
    int getServerPort();

    /**
     * 获取允许主动连接的最大连接数
     * @return
     */
    int getMaxOutConnect();

    /**
     * 获取允许主动连接的最小连接数
     * @return
     */
    int getMinOutConnect();

    /**
     * 获取允许被动连接的最大连接数
     * @return
     */
    int getMaxInConnect();

    /**
     * 获取允许被动连接的最小连接数
     * @return
     */
    int getMinInConnect();

    /**
     * 获取PRC服务的端口
     * @return
     */
    int getRPCServerPort();

    /**
     * 是否是创世节点
     */
    boolean isGenesisSeed();

    /**
     * 获取节点名称
     * @return
     */
    String getMySeedName();
}