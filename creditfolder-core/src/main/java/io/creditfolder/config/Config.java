package io.creditfolder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.Resource;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 01:45
 */
@Configuration
public class Config {

    @Profile("main")
    @Configuration
    static class MainConfig {
        @Bean("networkconfig")
        public NetworkConfig getMainNetWorkConfig() {
            return new MainNetworkConfig();
        }
    }


//    @Profile("test")
//    @Resource
//    public NetworkConfig getTestNetWorkConfig() {
//        return new  MainNetworkConfig();
//    }
}
