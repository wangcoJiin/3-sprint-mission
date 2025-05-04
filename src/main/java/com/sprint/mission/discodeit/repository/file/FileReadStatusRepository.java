package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    // 파일 저장할 디렉토리
    private static final String STORAGE_DIR = "user-data-improve/read-status";
    private static final Logger logger = Logger.getLogger(FileReadStatusRepository.class.getName());

    // 폴더 생성
    public FileReadStatusRepository() {
        try {
            Files.createDirectories(Paths.get(STORAGE_DIR));
        }
        catch (IOException e) {
            throw new RuntimeException("폴더 생성 실패: " + STORAGE_DIR, e);
        }
    }

    // 유저 아이디와 채널명을 사용해 파일 저장
    private Path getFilePath(UUID userId, UUID channelId) {
        String filename = userId + "_" + channelId;
        return Paths.get(STORAGE_DIR, filename);
    }

    // 저장
    @Override
    public boolean saveReadStatus(ReadStatus readStatus) {
        Path filePath = getFilePath(readStatus.getUserId(), readStatus.getChannelId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(readStatus);
            return true;
        }
        catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            logger.log(Level.SEVERE, filePath + " 경로에 파일을 생성할 수 없습니다 ", e);
            return false;
        }
        catch (IOException e) {
            // 그 외 IO 예외
            logger.log(Level.SEVERE, " 메시지 조회 상태 파일 저장 중 오류 발생 ", e);
            return false;
        }
    }

    // 유저 아이디로 조회
    @Override
    public Optional<ReadStatus> findReadStatusByUserId(UUID userId, UUID channelId) {
        Path filePath = getFilePath(userId, channelId);

        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            ReadStatus readStatus = (ReadStatus) ois.readObject();
            return Optional.of(readStatus);
        }
        catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "ReadStatus 읽기 실패", e);
            return Optional.empty();
        }
    }

    // readStatus 아이디로 조회
    @Override
    public Optional<ReadStatus> findReadStatusById(UUID id) {
        // "user-data-improve/read-status" 경로 아래의 모든 파일 읽어들이기
        try (Stream<Path> files = Files.list(Paths.get(STORAGE_DIR))) {
            return files
                    //파일인지 검사 (디렉토리나 특수 파일은 제외함)
                    .filter(path -> Files.isRegularFile(path))

                    // 파일마다 읽어서 id가 일치하는지 확인함.
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            ReadStatus status = (ReadStatus) ois.readObject();
                            // id가 일치하면 객체 반환하기, 아니면 null
                            if(status.getId().equals(id)){
                                return status;
                            }
                            else{
                                return null;
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            logger.log(Level.WARNING, "파일 읽기 실패: " + path.getFileName(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .findFirst();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "디렉토리 읽기 실패: " + STORAGE_DIR, e);
            return Optional.empty();
        }
    }

    // 유저 아이디로 조회
    @Override
    public List<ReadStatus> findUserReadStatus(UUID userId){
        List<ReadStatus> result = new ArrayList<>();

        // 폴더 하위의 파일을 하나씩 순회!
        try (DirectoryStream<Path> files = Files.newDirectoryStream(Paths.get(STORAGE_DIR))) {
            for (Path path : files) {
                // 파일이 아니면 읽지 않고 넘김
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                String filename = path.getFileName().toString();
                // readStatus 파일은 userId_channelId 형식으로 저장되어 있기 때문에
                // '_'를 기준으로 분리하기
                String[] tokens = filename.split("_");

                // 파일명이 userId_channelId로 올바른지 검사
                if (tokens.length >= 2) {
                    String fileUserId = tokens[0];

                    // userId랑 파일저장명 id가 동일하면 파일 읽어서 객체를 리스트에 add
                    if (fileUserId.equals(userId.toString())) {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            ReadStatus status = (ReadStatus) ois.readObject();
                            result.add(status);
                        } catch (IOException | ClassNotFoundException e) {
                            logger.log(Level.WARNING, "파일 읽기에 실패했습니다: " + filename, e);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "디렉토리 읽기 실패: " + STORAGE_DIR, e);
        }

        return result;
    }

    // 채널 아이디로 조회
    @Override
    public List<ReadStatus> findReadStatusByChannelId(UUID channelId) {
        List<ReadStatus> result = new ArrayList<>();

        // 폴더 하위의 파일을 하나씩 순회!
        try (DirectoryStream<Path> files = Files.newDirectoryStream(Paths.get(STORAGE_DIR))) {
            for (Path path : files) {
                // 파일이 아니면 읽지 않고 넘김
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                String filename = path.getFileName().toString();
                // readStatus 파일은 userId_channelId 형식으로 저장되어 있기 때문에
                // '_'를 기준으로 분리하기
                String[] tokens = filename.split("_");

                // 파일명이 userId_channelId로 올바른지 검사
                if (tokens.length >= 2) {
                    String fileChannelId = tokens[1];

                    // userId랑 파일저장명 id가 동일하면 파일 읽어서 객체를 리스트에 add
                    if (fileChannelId.equals(channelId.toString())) {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            ReadStatus status = (ReadStatus) ois.readObject();
                            result.add(status);
                        } catch (IOException | ClassNotFoundException e) {
                            logger.log(Level.WARNING, "파일 읽기에 실패했습니다: " + filename, e);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "디렉토리 읽기 실패: " + STORAGE_DIR, e);
        }

        return result;
    }

    // 업데이트 (저장과 동일함)
    @Override
    public boolean updateReadStatus(ReadStatus readStatus){
        return saveReadStatus(readStatus);
    }

    // readStatus 삭제
    @Override
    public boolean deleteReadStatusById(UUID id) {
        // "user-data-improve/read-status" 경로 아래의 모든 파일 읽어들이기
        try (Stream<Path> files = Files.list(Paths.get(STORAGE_DIR))) {
            Optional<Path> targetFile = files
                    //파일인지 검사 (디렉토리나 특수 파일은 제외함)
                    .filter(path -> Files.isRegularFile(path))

                    // 파일마다 읽어서 id가 일치하는지 확인함.
                    .filter(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            ReadStatus status = (ReadStatus) ois.readObject();
                            // id가 일치하면 객체 반환하기, 아니면 null
                            return status.getId().equals(id);
                        } catch (IOException | ClassNotFoundException e) {
                            logger.log(Level.WARNING, "파일 읽기 실패: " + path.getFileName(), e);
                            return false;
                        }
                    })
                    .findFirst();

            // 파일이 존재하면 삭제하기
            // deleteIfExists는 path에 파일이 존재하면 삭제하고 true 반환
            // 파일이 없다면 false 반환
            if(targetFile.isPresent()){
                return Files.deleteIfExists(targetFile.get());
            }
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "디렉토리 읽기 실패: " + STORAGE_DIR, e);
            return false;
        }
        return false;
    }
}
