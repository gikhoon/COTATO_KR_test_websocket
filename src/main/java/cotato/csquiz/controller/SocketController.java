package cotato.csquiz.controller;

import cotato.csquiz.controller.dto.socket.EducationCloseRequest;
import cotato.csquiz.controller.dto.socket.QuizOpenRequest;
import cotato.csquiz.controller.dto.socket.QuizSocketRequest;
import cotato.csquiz.controller.dto.socket.SocketTokenDto;
import cotato.csquiz.service.RecordService;
import cotato.csquiz.service.SocketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/socket")
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final SocketService socketService;
    private final RecordService recordService;

    @PatchMapping("/start/csquiz")
    public ResponseEntity<Void> openCSQuiz(@RequestBody @Valid QuizOpenRequest request) {
        socketService.openCSQuiz(request);
        recordService.saveAnswers(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/access")
    public ResponseEntity<Void> accessQuiz(@RequestBody @Valid QuizSocketRequest request) {
        socketService.accessQuiz(request);
        recordService.saveAnswer(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/start")
    public ResponseEntity<Void> startQuizSolve(@RequestBody @Valid QuizSocketRequest request) {
        socketService.startQuizSolve(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deny")
    public ResponseEntity<Void> denyQuiz(@RequestBody @Valid QuizSocketRequest request) {
        socketService.denyQuiz(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/stop")
    public ResponseEntity<Void> stopQuizSolve(@RequestBody @Valid QuizSocketRequest request) {
        socketService.stopQuizSolve(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/close/csquiz")
    public ResponseEntity<Void> stopAllQuiz(@RequestBody @Valid EducationCloseRequest request) {
        socketService.stopAllQuiz(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/token")
    public ResponseEntity<SocketTokenDto> makeSocketToken(@RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(socketService.createSocketToken(authorizationHeader));
    }
}