package io.creditfolder.message.processor;

import io.creditfolder.message.CommandDefinition;
import io.creditfolder.message.command.Message;
import io.creditfolder.message.MessageProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 类概述
 *
 * @author eleven@creditfolder.io
 * @since 2018年03月31日 19:08
 */
@Component
public class MessageProcessorFactory implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Autowired
    private UnknowMessageProcessor unknowMessageProcessor;

    public MessageProcessor getMessageProcessor(Message message) {
        Class<? extends MessageProcessor> clazz = CommandDefinition.MESSAGE_PROCESSOR.get(message.getClass());
        MessageProcessor processor = applicationContext.getBean(clazz);
        if (processor == null) {
            return unknowMessageProcessor;
        }
        return processor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
