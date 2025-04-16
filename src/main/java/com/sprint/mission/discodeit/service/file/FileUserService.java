package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

import java.io.*;
import java.util.stream.Collectors;

public class FileUserService implements UserService {

    private static final String FILE_PATH = "users.ser";

    /* 동작 순서
    * 1. FileOutputStream(FILE_PATH) -> 파일을 write 모드(파일 쓰기 스트림)로 열기.
    * 2. ObjectOutputStream(oos) -> 객체 저장용 스트림으로 감싸기
    * 3. oos.writeObject(users) -> Map<UUID, User> 객체를 직렬화해서 파일에 기록
    * 4. IOException e -> 직렬화 중 IO 예외 발생 시 스택트레이스 출력 */

    // 유저 맵 파일에 저장 (파일에 유저 객체 직렬화 하기)
    private void saveUsersToFile(Map<UUID, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 동작 순서
     * 1. FileInputStream(FILE_PATH) -> 파일을 바이너리 스트림으로 열기.
     * 2. ObjectInputStream (ois) -> 위에서 연 스트림을 객체 읽기용 스트림으로 감싸기
     * 3. ois.readObject() -> 파일에서 객체(Map<UUID, User>)를 읽어들임 (직렬화된 데이터를 자바 객체로 역직렬화 함)
     * 4. (Map<UUID, User>) -> object로 반환되므로 Map 타입캐스팅 해줌
     * 5. FileNotFoundException e -> 최초 실행시에 차일 없을 수도 있음 -> 빈 링크드해시맵 반환
     * 6. IOException | ClassNotFoundException e -> 역직렬화 실패 시 스택트레이스 출력하고 빈 링크드해시맵 반환 */

    // 파일에서 유저 맵 불러오기
    private Map<UUID, User> loadUsersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            // 런타임에 Map<?, ?> 처럼 작동해서 경고 띄우는 것.
            return (Map<UUID, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new LinkedHashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            //err 사용하면 표준 에러 스트림으로 출력되고 콘솔에서 빨갛게 강조됨
            System.err.println("파일 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }

    // 유저 생성, 파일에 저장
    @Override
    public User createUser(String name) {
        Map<UUID, User> users = loadUsersFromFile();
        User newUser = new User(name, "초기값");
        users.put(newUser.getId(), newUser);
        saveUsersToFile(users);
        return newUser;
    }

    // 기존 유저 리스트에 새로운 유저 추가
    @Override
    public void addUserToRepository(User user) {
        Map<UUID, User> users = loadUsersFromFile();
        users.put(user.getId(), user);
    }

    // 유저 아이디 이용해서 조회
    @Override
    public User getUserById(UUID id) {
        Map<UUID, User> users = loadUsersFromFile();
        return users.values().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 유저 이름 이용해서 조회
    @Override
    public List<User> searchUsersByName(String name) {
        Map<UUID, User> users = loadUsersFromFile();
        return users.values().stream()
                .filter(user -> user.getName().equals(name))
                .collect(Collectors.toList());
    }

    // 전체 유저 정보 조회
    @Override
    public List<User> getAllUsers() {
        Map<UUID, User> users = loadUsersFromFile();
        return new ArrayList<>(users.values());
    }

    // 유저 이름과 활동상태 둘 다 변경
    @Override
    public boolean updateUser(UUID id, String name, String connectState) {
        Map<UUID, User> users = loadUsersFromFile();
        User user = users.get(id);
        if (user == null){
            System.out.println("해당하는 유저가 없습니다.");
            return false;
        }

        user.updateName(name);
        user.updateConnectState(connectState);
        user.updateUpdatedAt(System.currentTimeMillis());

        saveUsersToFile(users);
        System.out.println("유저 이름과 활동 상태가 변경되었습니다.");
        return true;
    }

    // 유저 이름 변경
    @Override
    public boolean updateUserName(UUID id, String newName) {
        Map<UUID, User> users = loadUsersFromFile();
        User user = users.get(id);
        if (user == null){
            System.out.println("해당하는 유저가 없습니다.");
            return false;
        }
        System.out.println("이름 변경 중입니다... 변경 전: " + user.getName());
        user.updateName(newName);
        user.updateUpdatedAt(System.currentTimeMillis());

        System.out.println("이름 변경 성공. 변경 후: " + user.getName());
        saveUsersToFile(users);
        return true;
    }

    // 유저 활동상태 변경
    @Override
    public boolean updateConnectState(String name, String connectState) {
        Map<UUID, User> users = loadUsersFromFile();

        List<User> matchUser = users.values().stream()
                .filter(user -> user.getName().equals(name))
                .toList();

        if (matchUser.isEmpty()){
            System.out.println("해당하는 유저가 없습니다");
            return false;
        }

        if (matchUser.size() == 1){
            User user = matchUser.get(0);
            System.out.println("활동 상태 변경 중입니다... 변경 전: " + user.getConnectState());
            user.updateConnectState(connectState);
            user.updateUpdatedAt(System.currentTimeMillis());

            System.out.println("활동 상태 변경 성공. 변경 후: " + user.getConnectState());
            saveUsersToFile(users);
            return true;
        }

        System.out.println("조회된 유저가 두 명 이상입니다.");
        for (int i = 0; i < matchUser.size(); i++) {
            User user = matchUser.get(i);
            System.out.println("[" + i + "]" + " 생성 시간: " + user.getCreatedAt() + ", 수정 시간: " + user.getUpdatedAt() + ", 활동 상태: " + user.getConnectState());
        }

        System.out.print("수정을 원하는 유저의 번호를 입력해주세요.\n");
        Scanner scanner = new Scanner(System.in);
        int selection = scanner.nextInt();

        if (selection >= 0 && selection <= matchUser.size()) {
            User selectedUser = matchUser.get(selection);

            selectedUser.updateConnectState(connectState);
            selectedUser.updateUpdatedAt(System.currentTimeMillis());
            users.put(selectedUser.getId(), selectedUser);
            saveUsersToFile(users);
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
        Map<UUID, User> users = loadUsersFromFile();
        User user = users.get(id);

        if(user == null){
            System.out.println("해당하는 유저가 없습니다.");
            return false;
        }
        System.out.println("유저 삭제중입니다.");
        users.remove(id);
        System.out.println("유저가 삭제되었습니다.");
        saveUsersToFile(users);
        return true;
    }

    // 파일 삭제

}
