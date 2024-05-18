package cotato.csquiz.service;

import cotato.csquiz.controller.dto.AllEducationResponse;
import cotato.csquiz.controller.dto.education.CreateEducationRequest;
import cotato.csquiz.controller.dto.education.CreateEducationResponse;
import cotato.csquiz.controller.dto.education.EducationIdOfQuizResponse;
import cotato.csquiz.controller.dto.education.FindEducationStatusResponse;
import cotato.csquiz.controller.dto.education.UpdateEducationRequest;
import cotato.csquiz.controller.dto.education.WinnerInfoResponse;
import cotato.csquiz.controller.dto.quiz.KingMemberInfo;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.KingMember;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.domain.entity.Winner;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.KingMemberRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.SessionRepository;
import cotato.csquiz.repository.WinnerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EducationService {

    private final MemberService memberService;
    private final EducationRepository educationRepository;
    private final KingMemberRepository kingMemberRepository;
    private final QuizRepository quizRepository;
    private final WinnerRepository winnerRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public CreateEducationResponse createEducation(CreateEducationRequest request) {
        Session findSession = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
        checkEducationExist(request.sessionId());

        Education education = Education.builder()
                .sessionId(findSession.getId())
                .subject(request.subject())
                .educationNumber(request.educationNum())
                .generationId(findSession.getGeneration().getId())
                .build();

        Education saveEducation = educationRepository.save(education);
        return CreateEducationResponse.from(saveEducation);
    }

    private void checkEducationExist(Long sessionId) {
        if (educationRepository.existsBySessionId(sessionId)) {
            throw new AppException(ErrorCode.EDUCATION_DUPLICATED);
        }
    }

    public FindEducationStatusResponse findEducationStatus(Long educationId) {
        return FindEducationStatusResponse.from(findEducation(educationId));
    }

    private Education findEducation(Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateSubjectAndNumber(UpdateEducationRequest request) {
        validateNotEmpty(request.newSubject());
        Education education = educationRepository.findById(request.educationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
        education.updateSubject(request.newSubject());
        education.updateNumber(request.newNumber());
        educationRepository.save(education);
    }

    private void validateNotEmpty(String newSubject) {
        Optional.ofNullable(newSubject)
                .filter(subject -> !subject.trim().isEmpty())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_INVALID));
    }

    public List<AllEducationResponse> findEducationListByGeneration(Long generationId) {
        return findAllEducationByGenerationId(generationId).stream()
                .map(AllEducationResponse::from)
                .toList();
    }

    public List<KingMemberInfo> findKingMemberInfo(Long educationId) {
        Education findEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
        List<KingMember> kingMembers = kingMemberRepository.findAllByEducation(findEducation);
        validateIsEmpty(kingMembers);
        return kingMembers.stream()
                .map(kingMember -> memberService.findById(kingMember.getMemberId()))
                .map(member -> KingMemberInfo.from(member, memberService.findBackFourNumber(member)))
                .toList();
    }

    private void validateIsEmpty(List<KingMember> kingMembers) {
        if (kingMembers.isEmpty()) {
            throw new EntityNotFoundException("아직 결승 진출자가 결정되지 않았습니다.");
        }
    }

    public WinnerInfoResponse findWinner(Long educationId) {
        Education findEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
        Winner findWinner = winnerRepository.findByEducation(findEducation)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육의 우승자를 찾을 수 없습니다."));
        Member findMember = memberService.findById(findWinner.getMemberId());
        return WinnerInfoResponse.of(findWinner, findMember, memberService.findBackFourNumber(findMember));
    }

    public EducationIdOfQuizResponse findEducationIdOfQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("해당 문제를 찾을 수 없습니다."));
        return EducationIdOfQuizResponse.from(quiz);
    }

    public List<Education> findAllEducationByGenerationId(Long generationId) {
        return educationRepository.findAllByGenerationId(generationId);
    }
}

