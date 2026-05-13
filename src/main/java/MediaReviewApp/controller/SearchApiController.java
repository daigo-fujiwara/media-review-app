package MediaReviewApp.controller;

import MediaReviewApp.dto.MediaCandidate;
import MediaReviewApp.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // JSONを返すためのアノテーション
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchApiController {

    private final TmdbService tmdbService;

    @GetMapping("/movie") // メソッド名は searchMedia などに変えるとより適切です
    public List<MediaCandidate> search(@RequestParam String query, @RequestParam String type) {
        return tmdbService.searchCandidates(query, type);
    }
}