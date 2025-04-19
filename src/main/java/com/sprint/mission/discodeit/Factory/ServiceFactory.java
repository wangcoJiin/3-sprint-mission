package com.sprint.mission.discodeit.Factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class ServiceFactory {

    // 싱글톤 인스턴스 생성
    private static final ServiceFactory instance = new ServiceFactory();

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;


    private ServiceFactory() {
        this.userService = new JCFUserService();
        this.channelService = new JCFChannelService();
        this.messageService = new JCFMessageService(channelService, userService);
    }

    // 싱글톤 인스턴스 반환!
    public static ServiceFactory getInstance() {
        return instance;
    }

    public UserService getUserService() {
        return userService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
