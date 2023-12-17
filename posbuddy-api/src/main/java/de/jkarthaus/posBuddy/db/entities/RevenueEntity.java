package de.jkarthaus.posBuddy.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "revenues")
public class RevenueEntity {
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 36)
    @NotNull
    @Column(name = "posbuddyid", nullable = false, length = 36)
    private String posbuddyid;

    @Size(max = 10)
    @NotNull
    @Column(name = "itemid", nullable = true, length = 10)
    private String itemid;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @NotNull
    @Column(name = "value", nullable = false)
    private Float value;

    @NotNull
    @Column(name = "timeofsales", nullable = false)
    private Instant timeofsales;

}
