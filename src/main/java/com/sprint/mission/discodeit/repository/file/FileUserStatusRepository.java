package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    // 파일 저장할 디렉토리
    private static final String STORAGE_DIR = "user-data-improve/user-status";
    private static final Logger logger = Logger.getLogger(FileUserStatusRepository.class.getName());

    // 폴더 생성
    public FileUserStatusRepository() {
        try {
            Files.createDirectories(Paths.get(STORAGE_DIR));
        }
        catch (IOException e) {
            throw new RuntimeException("폴더 생성 실패: " + STORAGE_DIR, e);
        }
    }


    // 유저 상태 저장
    @Override
    public boolean saveUserStatus(UserStatus userStatus) {
        Path filePath = Paths.get(STORAGE_DIR, userStatus.getUserId().toString());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(userStatus);
            return true;
        }
        catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            logger.log(Level.SEVERE, filePath + " 경로에 파일을 생성할 수 없습니다 ", e);
            return false;
        }
        catch (IOException e) {
            // 그 외 IO 예외
            logger.log(Level.SEVERE, " 접속 상태 파일 저장 중 오류 발생 ", e);
            return false;
        }
    }


    // 특정 유저의 상태 조회
    @Override
    public Optional<UserStatus> findStatus(UUID userId) {
        Path filePath = Paths.get(STORAGE_DIR, userId.toString());

        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            UserStatus userStatus = (UserStatus) ois.readObject();
            return Optional.of(userStatus);
        }
        catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "UserStatus 읽기 실패", e);
            return Optional.empty();
        }
    }

    // 등록된 전체 상태 조회
    @Override
    public List<UserStatus> findAllStatus() {
        List<UserStatus> userStatusList = new ArrayList<>();
        try (Stream<Path> paths = Files.list(Paths.get(STORAGE_DIR))){
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            UserStatus userStatus = (UserStatus) ois.readObject();
                            userStatusList.add(userStatus);
                        } catch (IOException | ClassNotFoundException e) {
                            logger.log(Level.SEVERE, "UserStatus 파일 읽기 실패: " + path.getFileName(), e);
                        }
                    });
        }
        catch (IOException e){
            logger.log(Level.SEVERE, "UserStatus 폴더 읽기 실패: " + STORAGE_DIR, e);
        }

        return userStatusList;
    }

    // 유저 상태 업데이트
    @Override
    public boolean updateUserStatus(UserStatus userStatus) {
        Path filePath = Paths.get(STORAGE_DIR, userStatus.getUserId().toString());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(userStatus);
        }
        catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            logger.log(Level.SEVERE, filePath + " 경로에 파일을 생성할 수 없습니다 ", e);
            return false;
        }
        catch (IOException e) {
            // 그 외 IO 예외
            logger.log(Level.SEVERE, " 채널 파일 저장 중 오류 발생 ", e);
            return false;
        }
        return true;
    }

    // 유저 상태 삭제
    @Override
    public boolean deleteUserStatus(UUID userId) {
        Path filePath = Paths.get(STORAGE_DIR, userId.toString());

        if (!Files.exists(filePath)) {
            return false;
        }

        try {
            return Files.deleteIfExists(filePath);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "UserStatus 삭제 실패", e);
            return false;
        }

    }
}
