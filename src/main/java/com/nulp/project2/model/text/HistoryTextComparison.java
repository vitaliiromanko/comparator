package com.nulp.project2.model.text;

import com.nulp.project2.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "history_text_comparison")
public class HistoryTextComparison implements Comparable<HistoryTextComparison> {
    private static final String SEQ_NAME = "history_text_comparison_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Long id;

    @NotBlank
    @Column(name = "title_first_text")
    private String titleFirstText;

    @NotBlank
    @Column(name = "title_second_text")
    private String titleSecondText;

    @NotNull
    @Column(name = "shingle_length")
    private int shingleLength;

    @NotNull
    @Column(name = "comparison_result")
    private double comparisonResult;

    @Column(name = "comparison_date")
    private LocalDateTime comparisonDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @PrePersist
    protected void prePersist() {
        comparisonDate = LocalDateTime.now();
    }


    @Override
    public int compareTo(HistoryTextComparison o) {
        return getComparisonDate().compareTo(o.comparisonDate);
    }
}
