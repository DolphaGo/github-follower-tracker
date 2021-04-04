package me.dolphago.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.ChangeData;
import me.dolphago.domain.ChangeDataRepository;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class ChangeDataService {
    private final ChangeDataRepository changeDataRepository;

    public void saveAll(List<ChangeData> list) {
        log.info("[Changes log] {}", list);
        changeDataRepository.saveAll(list);
    }
}
