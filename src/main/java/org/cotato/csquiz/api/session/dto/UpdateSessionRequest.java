package org.cotato.csquiz.api.session.dto;

import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UpdateSessionRequest(
        @NotNull
        Long sessionId,
        MultipartFile sessionImage,
        @NotNull
        Boolean isPhotoUpdated,
        String description,
        @NotNull
        ItIssue itIssue,
        @NotNull
        Networking networking,
        @NotNull
        CSEducation csEducation,

        @NotNull
        DevTalk devTalk
) {
}
