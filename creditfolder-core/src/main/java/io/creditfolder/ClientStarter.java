package io.creditfolder;

import io.creditfolder.peer.PeerKeeper;
import io.creditfolder.peer.PeerServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 00:59
 */
@SpringBootApplication
public class ClientStarter implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    public static void main(String args[]) {
        // 寻找网络中的节点
        SpringApplication.run(ClientStarter.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PeerKeeper peerKeeper = applicationContext.getBean(PeerKeeper.class);
        peerKeeper.start();
    }
}
