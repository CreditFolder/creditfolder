package io.creditfolder.message.command;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月26日 18:07
 */
public abstract class Message {
    private static final Logger logger = LoggerFactory.getLogger(Message.class);

    public static final int MAX_SIZE = 0x02000000;

    protected Message() {

    }
}
