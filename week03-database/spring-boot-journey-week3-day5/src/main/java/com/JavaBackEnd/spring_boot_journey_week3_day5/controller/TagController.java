package com.JavaBackEnd.spring_boot_journey_week3_day5.controller;

import com.JavaBackEnd.spring_boot_journey_week3_day5.entity.Tag;
import com.JavaBackEnd.spring_boot_journey_week3_day5.repository.TagRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @GetMapping("/in-use")
    public List<Tag> getTagsInUse() {
        return tagRepository.findTagsInUse();
    }

    @GetMapping("/search")
    public List<Tag> searchTags(@RequestParam String keyword) {
        return tagRepository.findByNameContainingIgnoreCase(keyword);
    }
}