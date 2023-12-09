package com.nulp.project2.model.user;

import com.nulp.project2.util.GenerateSimpleTokenUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reset_password_token")
@Getter
@Setter
@ToString
public class ResetPasswordToken {
    private static final String SEQ_NAME = "reset_password_token_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Long id;
    @Column(name = "token")
    private String token;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void prePersist() {
        creationDate = LocalDateTime.now();
    }

    protected ResetPasswordToken() {
    }

    public static ResetPasswordToken of(User user) {
        return new ResetPasswordToken(null, GenerateSimpleTokenUtils.getToken(), user);
    }

    private ResetPasswordToken(Long id, String token, User user) {
        this.id = id;
        this.token = token;
        this.user = user;
    }
}
