package org.cotato.csquiz.common.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.cotato.csquiz.api.quiz.dto.QuizResponse;
import org.cotato.csquiz.api.socket.dto.CsQuizStopResponse;
import org.cotato.csquiz.api.socket.dto.EducationResultResponse;
import org.cotato.csquiz.api.socket.dto.QuizStartResponse;
import org.cotato.csquiz.api.socket.dto.QuizStatusResponse;
import org.cotato.csquiz.api.socket.dto.QuizStopResponse;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.education.enums.QuizStatus;
import org.cotato.csquiz.common.error.ErrorCode;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.domain.education.service.QuizService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, WebSocketSession> MANAGERS = new ConcurrentHashMap<>();
    private static final String KING_COMMAND = "king";
    private static final String WINNER_COMMAND = "winner";
    private static final String SHOW_COMMAND = "show";
    private static final String START_COMMAND = "start";
    private static final String EXIT_COMMAND = "exit";
    private static final String MEMBER_ID_KEY = "memberId";
    private static final String EDUCATION_ID_KEY = "educationId";
    private static final String ROLE_KEY = "role";
    private static final CloseStatus ATTEMPT_NEW_CONNECTION = new CloseStatus(4001, "new connection request");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuizRepository quizRepository;
    private final QuizService quizService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
//        String memberId = findAttributeByToken(session, MEMBER_ID_KEY);
        String memberId = UUID.randomUUID().toString();
        Long educationId = Long.parseLong(findAttributeByToken(session, EDUCATION_ID_KEY));
        String role = findAttributeByToken(session, ROLE_KEY);
        MemberRole memberRole = MemberRole.MEMBER;

        if (MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, memberRole)) {
            handleSessionReplacement(memberId, CLIENTS);
            CLIENTS.put(memberId, session);
        } else {
            handleSessionReplacement(memberId, MANAGERS);
            MANAGERS.put(memberId, session);
        }

        if (MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, memberRole)) {
            sendCurrentOpenQuiz(educationId, session);
        }

        log.info("[세션 연결] {}, 연결된 세션: {}", memberId, session.getId());
    }

    private void handleSessionReplacement(String memberId, ConcurrentHashMap<String, WebSocketSession> managers)
            throws IOException {
        if (managers.containsKey(memberId)) {
            managers.get(memberId).close(ATTEMPT_NEW_CONNECTION);
            managers.remove(memberId);
        }
    }

    private void sendCurrentOpenQuiz(Long educationId, WebSocketSession session) {
        Optional<Quiz> maybeQuiz = quizRepository.findByStatusAndEducationId(QuizStatus.QUIZ_ON,
                educationId);

        QuizStatusResponse response = maybeQuiz.map(quiz -> QuizStatusResponse.builder()
                        .command(SHOW_COMMAND)
                        .sendTime(LocalDateTime.now().toString())
                        .quizId(quiz.getId())
                        .status(quiz.getStatus())
                        .start(quiz.getStart())
                        .build())
                .orElse(QuizStatusResponse.builder()
                        .command(SHOW_COMMAND)
                        .build());
        sendMessage(session, response);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        String memberId = findAttributeByToken(session, MEMBER_ID_KEY);
//        String roleAttribute = findAttributeByToken(session, ROLE_KEY);
//        MemberRole memberRole = MemberRole.fromKey(roleAttribute);
//
//        if (MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, memberRole)) {
//            CLIENTS.remove(memberId);
//        } else {
//            MANAGERS.remove(memberId);
//        }
        log.info("[세션 종료] , 종료 코드: {}", status);
    }

    public void accessQuiz(Long quizId) {
        LocalDateTime now = LocalDateTime.now();
        QuizResponse quizData = quizService.findOneQuizForMember(quizId);

        QuizStatusResponse response = QuizStatusResponse.builder()
                .sendTime(now.toString())
                .quizId(quizId)
                .command(SHOW_COMMAND)
                .status(QuizStatus.QUIZ_ON)
                .start(QuizStatus.QUIZ_OFF)
                .build();

        log.info("[문제 {} 접근 허용]", quizId);
        log.info("[연결된 사용자 : {}]", CLIENTS.keySet());
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
        log.info("[문제 전송 후 사용자 : {}]", CLIENTS.keySet());
    }

    public void startQuiz(Long quizId) {
        QuizStartResponse response = QuizStartResponse.builder()
                .quizId(quizId)
                .command(START_COMMAND)
                .build();

        log.info("[문제 {} 풀이 허용]", quizId);
        log.info("[연결된 사용자 : {}]", CLIENTS.keySet());
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
        log.info("[풀이 신호 전송 후 사용자 : {}]", CLIENTS.keySet());
    }

    public void stopQuiz(Long quizId) {
        QuizStopResponse response = QuizStopResponse.from(quizId);
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    public void sendKingMemberCommand(Long educationId) {
        EducationResultResponse response = EducationResultResponse.of(KING_COMMAND, educationId);

        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    public void sendWinnerCommand(Long educationId) {
        EducationResultResponse response = EducationResultResponse.of(WINNER_COMMAND, educationId);

        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    public void stopEducation(Long educationId) {
        CsQuizStopResponse response = CsQuizStopResponse.from(EXIT_COMMAND, educationId);
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    private String findAttributeByToken(WebSocketSession session, String key) {
        return session.getAttributes().get(key).toString();
    }

    private void sendMessage(WebSocketSession session, Object sendValue) {
        try {
            String json = objectMapper.writeValueAsString(sendValue);
            TextMessage responseMessage = new TextMessage(json);
            session.sendMessage(responseMessage);
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }
}
