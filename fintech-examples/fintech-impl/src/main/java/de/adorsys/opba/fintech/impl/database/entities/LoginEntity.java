package de.adorsys.opba.fintech.impl.database.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class LoginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "login_generator")
    @SequenceGenerator(name = "login_generator", sequenceName = "login_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime loginTime;
}
