package com.knucse.diy.domain.persistence.key;

import com.knucse.diy.domain.model.key.RoomKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomKeyRepository extends JpaRepository<RoomKey,Long> {
    @Override
    Optional<RoomKey> findById(Long id);

    @Override
    List<RoomKey> findAll();
}
