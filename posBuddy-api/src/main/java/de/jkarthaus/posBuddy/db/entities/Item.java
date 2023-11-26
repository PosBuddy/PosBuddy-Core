package de.jkarthaus.posBuddy.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "items")
@Data
public class Item {
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
