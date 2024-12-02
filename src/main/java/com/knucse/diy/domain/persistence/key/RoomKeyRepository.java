package com.knucse.diy.domain.persistence.key;

import com.knucse.diy.domain.model.key.RoomKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomKeyRepository extends JpaRepository<RoomKey,Long> {
    @Override
    Optional<RoomKey> findById(Long id);

    @Override
    List<RoomKey> findAll();

    // id가 가장 작은 RoomKey 가져오기
    @Query("SELECT rk FROM RoomKey rk ORDER BY rk.id ASC LIMIT 1")
    Optional<RoomKey> findFirstKey();
}
