package org.cotato.csquiz.domain.generation.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(name = "session_number")
    private Integer number;

    @Column(name = "session_title", length = 100)
    private String title;

    @Column(name = "session_description")
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "generation_id")
    private Generation generation;

    @AttributeOverrides({
            @AttributeOverride(name = "itIssue",
                    column = @Column(name = "session_it_issue")),
            @AttributeOverride(name = "networking",
                    column = @Column(name = "session_networking")),
            @AttributeOverride(name = "csEducation",
                    column = @Column(name = "session_cs_education")),
            @AttributeOverride(name = "devTalk",
                    column = @Column(name = "session_dev_talk"))
    })
    private SessionContents sessionContents;

    @Column(name = "session_date")
    private LocalDate sessionDate;

    @Column(name = "session_place_name")
    private String placeName;

    @Builder
    public Session(Integer number, String title, String description, String placeName, LocalDate sessionDate,
                   Generation generation, SessionContents sessionContents) {
        this.number = number;
        this.title = title;
        this.description = description;
        this.placeName = placeName;
        this.sessionDate = sessionDate;
        this.generation = generation;
        this.sessionContents = sessionContents;
    }

    public void changeSessionNumber(Integer sessionNumber) {
        this.number = sessionNumber;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateSessionContents(SessionContents sessionContents) {
        this.sessionContents = sessionContents;
    }

    public void updateSessionTitle(String title) {
        this.title = title;
    }

    public void updateSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public void updateSessionPlace(String placeName) {
        this.placeName = placeName;
    }
}
