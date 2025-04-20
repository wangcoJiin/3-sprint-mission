package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

import java.io.*;
import java.util.stream.Collectors;

public class FileUserService implements UserService {

    //레포지토리 의존성
    private final UserRepository fileUserRepository;
    public FileUserService(UserRepository fileUserRepository) {
        this.fileUserRepository = fileUserRepository;
    }

    // 유저 생성, 파일에 저장
    @Override
    public User createUser(String name) {
        System.out.println("유저 생성중");
        User newUser = new User(name, "초기값");
        fileUserRepository.saveUser(newUser);
        return newUser;
    }

    // 기존 유저 리스트에 새로운 유저 추가
    @Override
    public void addUserToRepository(User user) {
        fileUserRepository.saveUser(user);
    }

    // 유저 아이디 이용해서 조회
    @Override
    public User getUserById(UUID id) {
        System.out.println("유저 아이디 이용해 조회: ");
        return fileUserRepository.findUserById(id);
    }

    // 유저 이름 이용해서 조회
    @Override
    public List<User> searchUsersByName(String name) {
        System.out.println("유저 이름 이용해 조회: ");
        return fileUserRepository.findUserByName(name);
    }

    // 전체 유저 정보 조회
    @Override
    public List<User> getAllUsers() {
        System.out.println("전체 유저 조회: ");
        return fileUserRepository.findUserAll();
    }

    // 유저 이름과 활동상태 둘 다 변경
    @Override
    public boolean updateUser(UUID id, String name, String connectState) {
        System.out.println("유저 이름과 활동상태 수정: ");
        User user = fileUserRepository.findUserById(id);
        if (user != null) {
            fileUserRepository.updateUserName(user, name);
            fileUserRepository.updateConnectState(user, connectState);
            System.out.println("유저의 이름, 활동상태가 수정됐습니다.");
            return true;
        }
        return false;
    }

    // 유저 이름 변경
    @Override
    public boolean updateUserName(UUID id, String newName) {
        System.out.println("유저 이름 변경: ");
        User user = fileUserRepository.findUserById(id);
        if (user != null) {
            fileUserRepository.updateUserName(user, newName);
            System.out.println("유저 이름이 변경되었습니다.");
            return true;
        }
        return false;
    }

    // 유저 활동상태 변경
    @Override
    public boolean updateConnectState(String name, String connectState) {
        System.out.println("유저 활동상태 변경: ");
        List<User> users = fileUserRepository.findUserByName(name);

        if (users.isEmpty()) {
            System.out.println("조회된 유저가 없습니다.");
            return false;
        }

        if (users.size() == 1) {
            System.out.println("활동상태 수정중");
            fileUserRepository.updateConnectState(users.get(0), connectState);
            return true;
        }
        System.out.println("조회된 유저가 두 명 이상입니다.");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println("[" + i + "]" + " 생성 시간: " + user.getCreatedAt() + ", 수정 시간: " + user.getUpdatedAt() + ", 활동 상태: " + user.getConnectState());
        }

        System.out.print("수정을 원하는 유저의 번호를 입력해주세요.\n");
        Scanner scanner = new Scanner(System.in);
        int selection = scanner.nextInt();

        if (selection >= 0 && selection <= users.size()) {
            User selectedUser = users.get(selection);
            fileUserRepository.updateConnectState(selectedUser, connectState);
            System.out.println("선택한 유저의 활동 상태가 변경되었습니다.");
            return true;

        } else {
            System.out.println("잘못된 번호입니다.");
            return false;
        }
    }

    // 유저 삭제
    @Override
    public boolean deleteUserById(UUID id) {
        System.out.println("유저 삭제: ");
        User user = fileUserRepository.findUserById(id);
        if (user == null) {
            System.out.println("조회된 유저가 없습니다.");
            return false;
        }
        fileUserRepository.deleteUser(id);
        System.out.println("유저가 삭제되었습니다.");
        return true;
    }

}
