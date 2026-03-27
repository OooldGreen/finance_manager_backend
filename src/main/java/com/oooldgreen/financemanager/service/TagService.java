package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.TagDTO;
import com.oooldgreen.financemanager.entity.Tag;
import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.mapper.TagMapper;
import com.oooldgreen.financemanager.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final UserService userService;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Transactional
    public List<TagDTO> getAllTags() {
        User user = userService.getCurrentAuthUser();
        List<Tag> tags = tagRepository.findAllByUserId(user.getId());
        return tagMapper.toDTOs(tags);
    }

    @Transactional
    public Tag getOrCreateTag(String tagName) {
        User user = userService.getCurrentAuthUser();
        return tagRepository.findByUserIdAndName(user.getId(), tagName)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    newTag.setUser(user);
                    return tagRepository.save(newTag);
                });
    }

    public List<TagDTO> getFrequentTags(int limit) {
        Long userId = userService.getCurrentAuthUser().getId();
        List<Tag> tags = tagRepository.findTopUsedTags(userId, PageRequest.of(0, limit));
        return tagMapper.toDTOs(tags);
    }
}
