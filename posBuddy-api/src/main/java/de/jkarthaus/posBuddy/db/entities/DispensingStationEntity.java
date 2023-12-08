package de.jkarthaus.posBuddy.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "dispensingStation")
@Table(name = "dispensingStations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispensingStationEntity {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "location", nullable = false, length = 40)
    private String location;


}
