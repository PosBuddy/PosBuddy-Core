package de.jkarthaus.posBuddy.db.entities;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Introspected
public class ItemEntity {
    @Id
    @Column(name = "artikel", nullable = false, length = Integer.MAX_VALUE)
    private String artikel;

    @Column(name = "einheit", nullable = false, length = Integer.MAX_VALUE)
    private String einheit;

    @Column(name = "minalter", nullable = false)
    private Integer minalter;

    @Column(name = "ausgabestation", nullable = false, length = Integer.MAX_VALUE)
    private String ausgabestation;

    @Column(name = "preis", nullable = false)
    private Double preis;

}
