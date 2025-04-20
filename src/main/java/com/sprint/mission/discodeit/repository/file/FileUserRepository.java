package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "userRepository.ser";

    private Map<UUID, User> users = loadUsersFromFile();

    // 유저 정보를 파일로 저장
    private void saveUsersToFile(Map<UUID, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 유저 파일 읽어오기
    private Map<UUID, User> loadUsersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new LinkedHashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("유저 파일 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }

    // 유저 저장
    @Override
    public void saveUser(User user) {
        users.put(user.getId(), user);
        saveUsersToFile(users);
    }

    //유저 read
    @Override
    public List<User> findUserAll() {
        return new ArrayList<>(users.values());
    }

    // 유저 read (id)
    @Override
    public User findUserById(UUID userId) {
        return users.get(userId);
    }

    // 유저 read (name)
    @Override
    public List<User> findUserByName(String userName) {
        return users.values().stream()
                .filter(user -> user.getName().equalsIgnoreCase(userName))
                .collect(Collectors.toList());
    }

    // 유저 이름 수정
    @Override
    public boolean updateUserName(User user, String newName){
        user.updateName(newName);
        user.updateUpdatedAt(System.currentTimeMillis());
        saveUser(user);

        return true;
    }

    // 유저 활동상태 수정
    @Override
    public boolean updateConnectState(User user, String connectState){
        user.updateConnectState(connectState);
        user.updateUpdatedAt(System.currentTimeMillis());
        saveUser(user);

        return true;
    }

    //유저 삭제
    @Override
    public void deleteUser(UUID userId) {
        users.remove(userId);
        saveUsersToFile(users);
    }
}
