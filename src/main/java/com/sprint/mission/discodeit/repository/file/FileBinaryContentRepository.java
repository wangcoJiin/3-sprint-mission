package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    // 파일 저장할 디렉토리
    private static final String STORAGE_DIR = "user-data-improve/binary-content";
    private static final Logger logger = Logger.getLogger(FileBinaryContentRepository.class.getName());


    // 폴더 생성
    public FileBinaryContentRepository() {
        try {
            Files.createDirectories(Paths.get(STORAGE_DIR));
        }
        catch (IOException e) {
            throw new RuntimeException("폴더 생성 실패: " + STORAGE_DIR, e);
        }
    }

    //바이너리 컨텐츠 저장
    @Override
    public boolean saveBinaryContent(BinaryContent binaryContent) {
        Path filePath = Paths.get(STORAGE_DIR, binaryContent.getId().toString());
        try (ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(filePath.toFile())))){
            oos.writeObject(binaryContent);
            System.out.println("바이너리 파일이 저장 되었습니다");
            return true;
        }
        catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            logger.log(Level.SEVERE, filePath + " 경로에 파일을 생성할 수 없습니다 ", e);
            return false;
        }
        catch (IOException e) {
            // 그 외 IO 예외
            logger.log(Level.SEVERE, " 바이너리 파일 저장 중 오류 발생 ", e);
            return false;
        }
    }

    // 파일 Id 로 검색
    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path filePath = Paths.get(STORAGE_DIR, id.toString());

        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            BinaryContent binaryContent = (BinaryContent) ois.readObject();
            return Optional.of(binaryContent);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(filePath + " 파일 읽기 실패 ", e);
        }
    }

    // 바이너리 파일 삭제
    @Override
    public boolean deleteById(UUID id) {
        Path filePath = Paths.get(STORAGE_DIR, id.toString());

        if (!Files.exists(filePath)) {
            return false;
        }
        try{
            Files.deleteIfExists(filePath);
            return true;
        } catch (IOException e){
            logger.log(Level.SEVERE, " 바이너리 파일 삭제 중 오류 발생 ", e);
            return false;
        }
    }
}
