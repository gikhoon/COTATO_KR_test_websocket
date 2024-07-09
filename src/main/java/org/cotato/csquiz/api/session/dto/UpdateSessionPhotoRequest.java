package org.cotato.csquiz.api.session.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Valid
public record UpdateSessionPhotoRequest(
        @NotNull
        Long sessionId,
        MultipartFile photo
) {
}
