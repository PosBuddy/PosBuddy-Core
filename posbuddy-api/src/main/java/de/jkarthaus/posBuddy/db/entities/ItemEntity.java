package de.jkarthaus.posBuddy.db.entities;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "items")
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Introspected
public class ItemEntity {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id;

    @Column(name = "itemtext", nullable = false, length = 40)
    private String itemText;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "minage", nullable = false, length = Integer.MAX_VALUE)
    private Integer minAge;

    @Column(name = "dispensingstation", nullable = false, length = 10)
    private String dispensingStationId;

    @Column(name = "price", nullable = false)
    private Double price;

}
