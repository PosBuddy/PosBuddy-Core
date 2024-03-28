package de.jkarthaus.posBuddy.db.entities;

import de.jkarthaus.posBuddy.model.enums.ConfigID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "config")
@Table(name = "config")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigEntity {

    @Enumerated(EnumType.STRING)
    @Id
    @Column(name = "id", nullable = false, length = 20)
    private ConfigID id;

    @Column(name = "jsonConfig", nullable = false)
    private String jsonConfig;


}
