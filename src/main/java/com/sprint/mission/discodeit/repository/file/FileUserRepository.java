package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 이메일 필드 생긴 테스트용 유저 레포지토리 구현체
 */

@Repository
public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "userRepositoryAdv.ser";

    private static final Logger logger = Logger.getLogger(FileUserRepository.class.getName());

    private final Map<UUID, User> users = loadUsersFromFile();

    // 유저 정보를 파일로 저장
    private boolean saveUsersToFile(Map<UUID, User> users) {

        try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fileOut)) {

            oos.writeObject(users);
            return true;

        } catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            logger.log(Level.SEVERE, FILE_PATH + "경로에 파일을 생성할 수 없습니다: ", e);
            return false;
        } catch (IOException e) {
            // 그 외 IO 예외
            logger.log(Level.SEVERE, "유저 파일 저장 중 오류 발생", e);
            return false;
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
    public boolean saveUser(User user) {
        users.put(user.getId(), user);

        // 유저 저장 상태 확인
        boolean success = saveUsersToFile(users);
        if (!success) {
            logger.warning("유저 저장에 실패했습니다: " + user.getId());
        }
        return success;
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
    public Optional<User> findUserByName(String userName) {
        return users.values().stream()
                .filter(user -> user.getName().equalsIgnoreCase(userName))
                .findFirst();
    }

    // 유저 조회 (이메일)
    @Override
    public Optional<User> findUserByEmail(String userEmail) {
        return users.values().stream()
                .filter(user -> user.getUserEmail().equalsIgnoreCase(userEmail))
                .findFirst();
    }

    // 유저 이름 수정
    @Override
    public boolean updateUserName(User user, String newName){
        user.updateName(newName);
        user.updateUpdatedAt(Instant.now());
        saveUser(user);

        return true;
    }

    //유저 삭제
    @Override
    public void deleteUser(UUID userId) {
        users.remove(userId);
    }
}
