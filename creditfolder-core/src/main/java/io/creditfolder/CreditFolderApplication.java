package io.creditfolder;

import io.creditfolder.peer.PeerKeeper;
import io.creditfolder.rpc.RPCServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportResource;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 00:59
 */
@SpringBootApplication
@ImportResource("classpath*:spring/*.xml")
public class CreditFolderApplication implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    public static void main(String args[]) {
        // 寻找网络中的节点
        SpringApplication.run(CreditFolderApplication.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PeerKeeper peerKeeper = applicationContext.getBean(PeerKeeper.class);
        peerKeeper.start();

        RPCServer rpcServer = applicationContext.getBean(RPCServer.class);
        rpcServer.startAsync();
    }
}
