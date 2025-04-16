package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private static final String FILE_PATH = "channel.ser";

    /* 동작 순서
     * 1. FileOutputStream(FILE_PATH) -> 파일을 write 모드(파일 쓰기 스트림)로 열기.
     * 2. ObjectOutputStream(oos) -> 객체 저장용 스트림으로 감싸기
     * 3. oos.writeObject(channels) -> Map<UUID, Channel> 객체를 직렬화해서 파일에 기록
     * 4. IOException e -> 직렬화 중 IO 예외 발생 시 스택트레이스 출력 */

    // 채널 맵을 파일에 저장 (파일에 채널 객체 직렬화 하기)
    private void saveChannelsToFile(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 동작 순서
     * 1. FileInputStream(FILE_PATH) -> 파일을 바이너리 스트림으로 열기.
     * 2. ObjectInputStream (ois) -> 위에서 연 스트림을 객체 읽기용 스트림으로 감싸기
     * 3. ois.readObject() -> 파일에서 객체(Map<UUID, Channel>)를 읽어들임 (직렬화된 데이터를 자바 객체로 역직렬화 함)
     * 4. (Map<UUID, Channel>) -> object로 반환되므로 Map 으로 타입캐스팅 해줌
     * 5. FileNotFoundException e -> 최초 실행시에 차일 없을 수도 있음 -> 빈 링크드해시맵 반환
     * 6. IOException | ClassNotFoundException e -> 역직렬화 실패 시 스택트레이스 출력하고 빈 링크드해시맵 반환 */

    // 파일에서 채널 맵 불러오기
    private Map<UUID, Channel> loadChannelsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new LinkedHashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            //err 사용하면 표준 에러 스트림으로 출력되고 콘솔에서 빨갛게 강조됨
            System.err.println("파일 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }

    // 채널 생성, 파일에 저장
    @Override
    public Channel createChannel(String channelName, UUID adminId, boolean isLock, String password) {
        Map<UUID, Channel> channels = loadChannelsFromFile();
        Channel newchannel = new Channel(channelName, adminId, isLock, password);
        channels.put(newchannel.getId(), newchannel);
        saveChannelsToFile(channels);
        addUserToChannel(newchannel.getId(), adminId, password);
        return newchannel;
    }

    // 채널에 유저 추가
    @Override
    public boolean addUserToChannel(UUID channelId, UUID userId, String password) {
        Map<UUID, Channel> channels = loadChannelsFromFile();

        Channel channel = channels.get(channelId);

        if((isChannelExist(channel))&& (isChannelLock(channel, password))) {

            channel.getJoiningUsers().add(userId);
            saveChannelsToFile(channels);
            System.out.println(userId + " 유저가 추가되었습니다");
            channel.updateUpdatedAt(System.currentTimeMillis());
            return true;
        }

        return false;
    }

    // 전체 채널 조회
    @Override
    public List<Channel> getAllChannels() {
        Map<UUID, Channel> channels = loadChannelsFromFile();
        return new ArrayList<>(channels.values());
    }

    // 이름으로 채널 조회
    @Override
    public List<Channel> getChannelUsingName(String channelName) {
        Map<UUID, Channel> channels = loadChannelsFromFile();
        return channels.values().stream()
                .filter(channel -> channel.getChannelName().equals(channelName))
                .toList();
    }

    // 채널 아이디로 조회
    @Override
    public Channel getChannelUsingId(UUID channelId) {
        Map<UUID, Channel> channels = loadChannelsFromFile();
        return channels.get(channelId);
    }

    // 채널 이름 수정
    @Override
    public boolean updateChannelName(UUID channelId, UUID userId, String password, String newChannelName) {
        Map<UUID, Channel> channels = loadChannelsFromFile();
        Channel channel = channels.get(channelId);

        //채널 유효성 검사
        if((isChannelExist(channel))&& (isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

            channel.updateChannelName(newChannelName);
            channel.updateUpdatedAt(System.currentTimeMillis());
            saveChannelsToFile(channels);
            System.out.println("채널 이름이 변경되었습니다.");
            return true;
        }

        return false;
    }

    // 채널 공개 상태 수정
    @Override
    public boolean updateChannelPrivateState(UUID channelId, UUID userId, String password, boolean isLock) {
        Map<UUID, Channel> channels = loadChannelsFromFile();
        Channel channel = channels.get(channelId);

        //채널 유효성 검사
        if((isChannelExist(channel)) && (isChannelAdmin(channel, userId))){

            if(channel.isLock() == isLock){
                System.out.println("변경할 사항이 없습니다.");
                return false;
            }
            if ((channel.isLock())) {
                System.out.println("비밀번호 확인중입니다.");
                if (!channel.getPassword().equals(password)) {
                    System.out.println("비밀번호가 일치하지 않습니다.");
                    return false;
                }
                System.out.println("공개 상태로 전환됩니다.");
                channel.updateIsLock(false);
                channel.updatePassword("");
            }
            else {
                System.out.println("비공개 상태로 전환됩니다.");
                channel.updateIsLock(true);
                channel.updatePassword(password);
            }
            channel.updateUpdatedAt(System.currentTimeMillis());
            System.out.println("채널 공개 상태가 수정되었습니다.");
            saveChannelsToFile(channels);
            return true;
        }
        return false;
    }

    // 채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId, UUID userId, String password) {
        Map<UUID, Channel> channels = loadChannelsFromFile();
        Channel channel =  channels.get(channelId);

        //채널 유효성 검사
        if((isChannelExist(channel))&& (isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

            channels.remove(channelId);
            saveChannelsToFile(channels);
            System.out.println("채널이 삭제되었습니다.");
            return true;
        }

        return false;
    }

    // 채널에서 참여 유저 삭제하기
    @Override
    public boolean deleteUserInChannel(UUID channelId, UUID adminId, UUID userId, String password) {
        Map<UUID, Channel> channels = loadChannelsFromFile();
        Channel channel = channels.get(channelId);

        //채널 유효성 검사
        if((isChannelExist(channel))&& (isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

            channel.getJoiningUsers().remove(userId);
            channel.updateUpdatedAt(System.currentTimeMillis());
            saveChannelsToFile(channels);
            System.out.println(channel.getChannelName() + "의"  + "참여자가 삭제되었습니다.");
            return true;
        }

        return false;
    }

    // 채널 유효성 검사 - 존재여부
    private boolean isChannelExist(Channel channel){
        if(channel == null){
            System.out.println("채널이 존재하지 않습니다.");
            return false;
        }
        return true;
    }
    // 채널 유효성 검사 - 관리자 대조
    private boolean isChannelAdmin(Channel channel, UUID userId){
        if(!channel.getAdminId().equals(userId)){
            System.out.println("채널 정보 수정 권한이 없습니다.");
            return false;
        }
        return true;
    }
    // 채널 유효성 검사 - 비밀번호 대조
    private boolean isChannelLock(Channel channel, String password){
        if(channel.isLock()){
            System.out.println("비공개 채널입니다. 비밀번호를 확인중입니다.");
            if(!channel.getPassword().equals(password)){
                System.out.println("비밀번호가 일치하지 않습니다.");
                return false;
            }
        }
        return true;
    }
}
