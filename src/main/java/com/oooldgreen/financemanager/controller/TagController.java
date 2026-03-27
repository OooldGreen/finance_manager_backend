package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.TagDTO;
import com.oooldgreen.financemanager.entity.Tag;
import com.oooldgreen.financemanager.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/frequent")
    public ResponseEntity<List<TagDTO>> getFrequentTags(@RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(tagService.getFrequentTags(limit));
    }
}
