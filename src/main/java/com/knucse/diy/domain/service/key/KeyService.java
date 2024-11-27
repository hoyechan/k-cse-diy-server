package com.knucse.diy.domain.service.key;

import com.knucse.diy.api.key.dto.KeyCreateDto;
import com.knucse.diy.api.key.dto.KeyReadDto;
import com.knucse.diy.api.key.dto.KeyStatusUpdateDto;
import com.knucse.diy.domain.exception.key.KeyDuplicatedException;
import com.knucse.diy.domain.exception.key.KeyNotFoundException;
import com.knucse.diy.domain.model.key.Key;
import com.knucse.diy.domain.persistence.key.KeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeyService {

    private final KeyRepository keyRepository;

    /**
     * 모든 Key를 조회합니다.
     * @return 조회된 모든 Key 혹은 empty List
     */
    private List<Key> findAllKey(){
        return keyRepository.findAll();
    }

    /**
     * KeyId를 기반으로 Key를 조회합니다.
     * @param id Long
     * @return 조회된 Key
     * @throws KeyNotFoundException "KEY_NOT_FOUND"
     */
    private Key findKeyById(Long id){
        return keyRepository.findById(id)
                .orElseThrow(KeyNotFoundException::new);
    }

    /**
     * KeyStatusUpdateDto를 기반으로 KeyStaus를 수정합니다.
     * @param keyStatusUpdateDto KeyStatusUpdateDto
     * @throws KeyNotFoundException "KEY_NOT_FOUND"
     */
    @Transactional
    private void updateKeyStatus(KeyStatusUpdateDto keyStatusUpdateDto){
        Key key = findKeyById(keyStatusUpdateDto.keyId());
        key.updateStatus(keyStatusUpdateDto.status());
    }


    /**
     * keyId를 기반으로 key를 삭제합니다.
     * @param keyId Long
     * @throws KeyNotFoundException "KEY_NOT_FOUND"
     */
    @Transactional
    public void deleteReservation(Long keyId){
        Key key = keyRepository.findById(keyId)
                .orElseThrow(KeyNotFoundException::new);

        keyRepository.delete(key);
    }

}
