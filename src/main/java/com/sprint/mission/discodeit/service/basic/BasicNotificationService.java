package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.CustomAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "notificationsByUser", key = "#receiverId")
    public List<NotificationDto> findAllNotifications(UUID receiverId) {
       return notificationRepository.findAllByReceiverId(receiverId)
           .stream()
           .map(notificationMapper::toDto)
           .toList();
    }

    @Override
    @Transactional
    public void deleteNotification(UUID notificationId, UUID requestId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() ->new NotificationNotFoundException(notificationId));

        // 본인의 알림만 삭제 가능
        if (!notification.getReceiver().getId().equals(requestId)) {
            throw new CustomAccessDeniedException();
        }

        notificationRepository.deleteById(notificationId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAllNotifications(List<Notification> notifications) {
        notificationRepository.saveAll(notifications);
    }

    // 관리자에게 알림
    @Override
    @Transactional
    public void notifyS3StoreFail(String jobName, UUID binaryContentId, String requestId,
        String errorMessage) {

        // 관리자 모두 조회
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        if (admins.isEmpty()) {
            log.warn("관리자 계정이 없습니다");
        }
        else {
            // 알림 구성
            String content = String.format("RequestId: %s \n BinaryContentId: %s \n Error: %s",
                requestId, binaryContentId, errorMessage);

            List<Notification> notifications = admins.stream()
                .map(admin -> new Notification(admin, jobName, content))
                .toList();

            try {
                saveAllNotifications(notifications);
            } catch (Exception saveEx) {
                log.error("실패 알림 저장 중 오류", saveEx);
            }
        }
    }
}
