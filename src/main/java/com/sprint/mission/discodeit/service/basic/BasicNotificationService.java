package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.auth.CustomAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
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
}
