package com.knucse.diy.domain.persistence.key;

import com.knucse.diy.domain.model.key.Key;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeyRepository extends JpaRepository<Key,Long> {
    @Override
    Optional<Key> findById(Long id);

    @Override
    List<Key> findAll();
}
