package com.nulp.project2.model.text;

import com.nulp.project2.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "history_file_comparison")
public class HistoryFileComparison implements Comparable<HistoryFileComparison> {
    private static final String SEQ_NAME = "history_file_comparison_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Long id;

    @OneToMany(targetEntity = FileComparisonResult.class, mappedBy = "historyFileComparison",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    @ToString.Exclude
    private Set<FileComparisonResult> fileComparisonResults = new LinkedHashSet<>();

    @NotNull
    @Column(name = "shingle_length")
    private int shingleLength;

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
    public int compareTo(HistoryFileComparison o) {
        return getComparisonDate().compareTo(o.getComparisonDate());
    }
}
