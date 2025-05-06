package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    // 생성
    @Override
    public ReadStatus createReadStatus(ReadStatusCreateRequest request) {

        // 유저 존재하지 않으면 예외 발생
        User findUser = userRepository.findUserById(request.userId());
        if (findUser == null){
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 채널 존재하지 않으면 예외 발생
        Channel findChannel = channelRepository.findChannelUsingId(request.channelId());
        if (findChannel == null){
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        // User + Channel 조합이 이미 존재하는지 확인
        List<ReadStatus> alreadyExistsUserId = readStatusRepository.findUserReadStatus(request.userId());
        boolean alreadyExists = alreadyExistsUserId.stream()
                .anyMatch(status -> status.getChannelId().equals(request.channelId()));

        if (alreadyExists) {
            throw new IllegalStateException("이미 존재하는 ReadStatus 입니다.");
        }

        // 객체 생성
        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());

        // 저장
        boolean save = readStatusRepository.saveReadStatus(readStatus);
        if (!save){
            throw new IllegalStateException("ReadStatus 저장에 실패했습니다.");
        }

        return readStatus;
    }

    // id로 readStatus 조회
    @Override
    public Optional<ReadStatus> findReadStatusById(UUID id) {
        Optional<ReadStatus> result = readStatusRepository.findReadStatusById(id);

        if(result.isEmpty()){
            throw new IllegalStateException("해당하는 ReadStatus가 없습니다.");
        }
        System.out.println("ReadStatus 조회 성공");
        return result;
    }

    // user id로 ReadStatus 조회
    @Override
    public List<ReadStatus> findReadStatusByUserId(UUID userId) {
        List<ReadStatus> result = readStatusRepository.findUserReadStatus(userId);

        if(result == null){
            throw new IllegalStateException("해당하는 유저의 ReadStatus가 없습니다.");
        }
        System.out.println("유저의 ReadStatus 조회 성공");
        return result;
    }

    // ReadStatus 업데이트
    @Override
    public boolean updateReadStatus(ReadStatusUpdateRequest request) {
        // id로 ReadStatus 조회
        Optional<ReadStatus> result = readStatusRepository.findReadStatusById(request.id());

        if(result.isEmpty()){
            throw new IllegalArgumentException("해당하는 ReadStatus가 없습니다.");
        }

        ReadStatus readStatus = result.get();

        readStatus.updateUpdatedAt(request.updatedAt());

        boolean update =  readStatusRepository.updateReadStatus(readStatus);
        if(!update){
            throw new IllegalStateException("ReadStatus 업데이트에 실패했습니다.");
        }

        return update;
    }

    // 삭제
    @Override
    public boolean deleteReadStatus(UUID id) {

        boolean delete = readStatusRepository.deleteReadStatusById(id);
        if(!delete){
            throw new IllegalStateException("ReadStatus 삭제에 실패했습니다.");
        }

        return delete;
    }
}
