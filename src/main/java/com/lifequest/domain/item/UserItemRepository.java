package com.lifequest.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    List<UserItem> findByUserId(Long userId);
    List<UserItem> findByUserIdAndEquippedTrue(Long userId);
    Optional<UserItem> findByUserIdAndItemSlot(Long userId, ItemSlot slot);
    Optional<UserItem> findByIdAndUserId(Long id, Long userId);
}
