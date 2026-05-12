package MediaReviewApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbResult(
        @JsonProperty("poster_path") String posterPath
) {}