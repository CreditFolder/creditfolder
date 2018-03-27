package io.creditfolder.config;

import io.creditfolder.peer.Seed;

import java.util.List;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:06
 */
public interface NetworkConfig {

    /**
     * 获取所有的种子
     * @return
     */
    List<Seed> getAllSeed();

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
     * 获取允许被动连接的最大连接数
     * @return
     */
    int getMaxInConnect();
}
