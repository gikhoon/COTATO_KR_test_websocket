package org.cotato.csquiz.api.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.project.dto.ProjectDetailResponse;
import org.cotato.csquiz.api.project.dto.ProjectSummaryResponse;
import org.cotato.csquiz.domain.generation.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "프로젝트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "특정 프로젝트 상세 정보 조회 API")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectDetail(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok().body(projectService.getProjectDetail(projectId));
    }

    @Operation(summary = "프로젝트 목록 조회 API")
    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getAllProjectSummaries() {
        return ResponseEntity.ok(projectService.getAllProjectSummaries());
    }
}
