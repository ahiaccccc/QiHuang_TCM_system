package com.example.qihuangserver.service;


import com.example.qihuangserver.model.AdminCollectedDTO;
import com.example.qihuangserver.model.Collected;
import com.example.qihuangserver.repository.CollectedRepository;
import com.example.qihuangserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCollectedService {
    private final CollectedRepository collectedRepository;
    private final UserRepository userRepository;

    public Page<AdminCollectedDTO> getAllCollections(Long userId, Long classicId, int page, int size) {
        Page<Collected> result = collectedRepository.findCollections(
                userId,
                classicId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );

        return result.map(collected -> {
            AdminCollectedDTO dto = new AdminCollectedDTO();
            dto.setCollectedId(collected.getId());
            dto.setUserId(collected.getUser().getId());
            dto.setUserName(collected.getUser().getUsername());
            dto.setClassicId(collected.getClassic().getId());
            dto.setClassicTitle(collected.getClassic().getTitle());
            dto.setCollectedTitle(collected.getTitle());
            return dto;
        });
    }


//    public void deleteCollection(Long id) {
//        collectedRepository.deleteById(id);
//    }
}