package de.jkarthaus.posBuddy.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "identity")
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
    private LocalDate startallocation;

    @Column(name = "endallocation")
    private LocalDate endallocation;

    @NotNull
    @Column(name = "balance", nullable = false)
    private Float balance;

}
