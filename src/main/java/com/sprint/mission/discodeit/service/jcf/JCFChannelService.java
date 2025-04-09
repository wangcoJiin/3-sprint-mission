package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private Map<UUID, Channel> channels = new HashMap<>();

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID adminId, boolean lockState, String password) {
        Channel newChannel = new Channel(channelName, adminId, lockState, password);
        channels.put(newChannel.getId(), newChannel);
        // 채널 생성과 동시에 참여자에 관리자도 추가
        addUserToChannel(newChannel.getId(), adminId, password);
        return newChannel;
    }

    // 모든 채널 조회
    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(channels.values());
    }

    // 이름만으로 채널 조회
    @Override
    public List<Channel> getChannelUsingName(String channelName) {
        return channels.values().stream()
                .filter(channel -> channel.getChannelName().equalsIgnoreCase(channelName))
                .collect(Collectors.toList());
    }

    // id로 채널 조회
    @Override
    public Channel getChannelUsingId(UUID channelId) {
        return channels.get(channelId);
    }

    // 채널 이름 수정
    @Override
    public boolean updateChannelName(UUID channelId, UUID userId, String password, String newChannelName) {
        Channel channel = channels.get(channelId);

        if (channel != null) {
            if (channel.getAdminId() == userId) {
                System.out.println("채널 관리자로 확인되었습니다.");

                // 비공개 채널인 경우
                if ((channel.isLock())) {
                    System.out.println("비밀번호 확인중입니다.");
                    if (Objects.equals(channel.getPassword(), password)) {
                        channel.updateChannelName(newChannelName);
                        channel.updateUpdatedAt(System.currentTimeMillis());
                        System.out.println("채널 이름 수정이 완료되었습니다.");
                        return true;
                    } else {
                        System.out.println("비밀번호가 일치하지 않습니다.");
                        return false;
                    }
                }
                // 공개 채널인 경우
                else {
                    channel.updateChannelName(newChannelName);
                    channel.updateUpdatedAt(System.currentTimeMillis());
                    System.out.println("채널 이름 수정이 완료되었습니다.");
                    return true;
                }

            } else {
                System.out.println("채널 이름 수정 권한이 없습니다.");
            }
        } else {
            System.out.println("채널이 비어있습니다");
        }
        return false;
    }

    //채널 공개/비공개 상태 수정
    @Override
    public boolean updateChannelPrivateState(UUID channelId, String channelName, UUID userId, String password, boolean lockState) {
        Channel channel = channels.get(channelId);

        if (channel != null) {

            if (channel.getAdminId() == userId) {
                System.out.println("채널 관리자로 확인되었습니다.");
                if ((channel.isLock()) == lockState) {
                    System.out.println("변경할 사항이 없습니다.");
                } else {
                    if ((channel.isLock())) {
                        System.out.println("비밀번호 확인중입니다.");
                        if (Objects.equals(channel.getPassword(), password)) {
                            if (!lockState) {
                                System.out.println("공개 상태로 전환됩니다.");
                                channel.updateIsLock(false);
                                channel.updatePassword("");
                                channel.updateUpdatedAt(System.currentTimeMillis());
                                System.out.println("채널 공개 상태가 수정되었습니다.");

                            }
                        }
                    } else {
                        System.out.println("비공개 상태로 전환됩니다.");
                        channel.updateIsLock(true);
                        channel.updatePassword(password);
                        channel.updateUpdatedAt(System.currentTimeMillis());
                    }

                }
            } else {
                System.out.println("채널 상태 수정 권한이 없습니다.");
            }
        } else {
            System.out.println("채널이 비어있습니다.");
        }
        return false;
    }


    // 채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId, String channelName, UUID userId, String password) {
        Channel channel = channels.get(channelId);

        if (channel != null) {
            if (channel.getAdminId() == userId) {
                System.out.println("채널 관리자로 확인되었습니다.");

                // 비공개 채널인 경우
                if ((channel.isLock())) {
                    System.out.println("비밀번호 확인중입니다.");
                    if (Objects.equals(channel.getPassword(), password)) {
                        channels.remove(channelId);
                        System.out.println("채널이 삭제되었습니다.");
                        return true;
                    } else {
                        System.out.println("비밀번호가 일치하지 않습니다.");
                        return false;
                    }
                }
                // 공개 채널인 경우
                else {
                    channels.remove(channelId);
                    System.out.println("채널이 삭제되었습니다.");
                    return true;
                }

            } else {
                System.out.println("\n채널 삭제 권한이 없습니다.");
            }
        } else {
            System.out.println("\n채널이 비어있습니다");
        }

        return false;
    }

    // 채널에 유저 추가
    @Override
    public boolean addUserToChannel(UUID channelId, UUID userId, String password) {
        Channel channel = channels.get(channelId);

        if (channel != null) {
            if ((channel.isLock())) {
                System.out.println("비밀번호 확인중입니다.");
                if (Objects.equals(channel.getPassword(), password)) {
                    channel.getJoiningUsers().add(userId);
                    System.out.println(userId + " 유저가 추가되었습니다");
                    channel.updateUpdatedAt(System.currentTimeMillis());
                } else {
                    System.out.println("비밀번호가 일치하지 않습니다.");
                }
            } else {
                channel.getJoiningUsers().add(userId);
                System.out.println(userId + " 유저가 추가되었습니다");
                channel.updateUpdatedAt(System.currentTimeMillis());
            }
        } else {
            System.out.println("채널이 비어있습니다.");
        }
        return false;
    }


    // 참여한 유저 삭제
    @Override
    public boolean deleteUserInChannel(UUID channelId, UUID adminId, UUID userId, String password) {
        Channel channel = channels.get(channelId);

        if (channel != null) {

            if (channel.getAdminId() == adminId) {
                System.out.println("채널 관리자로 확인되었습니다.");
                if ((channel.isLock())) {
                    System.out.println("비밀번호 확인중입니다.");
                    if (Objects.equals(channel.getPassword(), password)) {
                        channel.getJoiningUsers().remove(userId);
                        System.out.println(userId + " 유저가 삭제되었습니다");
                        channel.updateUpdatedAt(System.currentTimeMillis());
                    } else {
                        System.out.println("비밀번호가 일치하지 않습니다.");
                    }
                } else {
                    channel.getJoiningUsers().remove(userId);
                    System.out.println(userId + " 유저가 삭제되었습니다.");
                    channel.updateUpdatedAt(System.currentTimeMillis());
                }
            } else {
                System.out.println("유저 삭제 권한이 없습니다.");
            }
        } else {
            System.out.println("채널이 비어있습니다.");
        }
        return false;
    }
}