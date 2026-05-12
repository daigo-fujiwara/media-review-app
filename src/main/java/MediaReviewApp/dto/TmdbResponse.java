package MediaReviewApp.dto;

import java.util.List;

public record TmdbResponse(
        List<TmdbResult> results
) {}