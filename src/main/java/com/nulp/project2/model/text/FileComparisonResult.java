package com.nulp.project2.model.text;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "file_comparison_result")
@Getter
@Setter
@ToString
public class FileComparisonResult {
    private static final String SEQ_NAME = "file_comparison_result_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "history_file_comparison_id")
    private HistoryFileComparison historyFileComparison;

    @Column(name = "name_first_file")
    private String nameFirstFile;

    @Column(name = "name_second_file")
    private String nameSecondFile;

    @Column(name = "comparison_result")
    private double comparisonResult;

    protected FileComparisonResult() {
    }

    public static FileComparisonResult of(HistoryFileComparison historyFileComparison, String nameFirstFile,
                                          String nameSecondFile, double comparisonResult) {
        return new FileComparisonResult(null, historyFileComparison, nameFirstFile, nameSecondFile, comparisonResult);
    }

    public FileComparisonResult(Long id, HistoryFileComparison historyFileComparison, String nameFirstFile,
                                String nameSecondFile, double comparisonResult) {
        this.id = id;
        this.historyFileComparison = historyFileComparison;
        this.nameFirstFile = nameFirstFile;
        this.nameSecondFile = nameSecondFile;
        this.comparisonResult = comparisonResult;
    }
}
