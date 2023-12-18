package de.jkarthaus.posBuddy.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "identity")
@Entity(name = "identity")
@AllArgsConstructor
@NoArgsConstructor
public class IdentityEntity {
    @Id
    @Size(max = 36)
    @NotNull
    @Column(name = "posbuddyid", nullable = false, length = 36)
    private String posbuddyid;

    @Size(max = 40)
    @Column(name = "surname", length = 40)
    private String surname;

    @Size(max = 40)
    @Column(name = "lastname", length = 40)
    private String lastname;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Size(max = 40)
    @Column(name = "atribute1", length = 40)
    private String atribute1;

    @Size(max = 40)
    @Column(name = "atribute2", length = 40)
    private String atribute2;

    @Size(max = 40)
    @Column(name = "atribute3", length = 40)
    private String atribute3;

    @Column(name = "startallocation")
    private LocalDateTime startallocation;

    @Column(name = "endallocation")
    private LocalDateTime endallocation;


    @NotNull
    @Column(name = "balance", nullable = false)
    private Float balance;


}
