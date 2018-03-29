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
     * 是否种子：如果是种子节点，一开始不需要连接其他节点，常说中的第一个节点
     */
    boolean isSeed();

    /**
     * 打印配置相关信息
     */
    void showInfo();

    /**
     * 获取节点名称
     * @return
     */
    String getMySeedName();
}
