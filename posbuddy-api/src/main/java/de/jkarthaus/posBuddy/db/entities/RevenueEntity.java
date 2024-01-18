package de.jkarthaus.posBuddy.db.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name= "revenues")
@Table(name = "revenues")
public class RevenueEntity {
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Size(max = 36)
    @NotNull
    @Column(name = "posbuddyid", nullable = false, length = 36)
    private String posbuddyid;

    @Size(max = 10)
    @Column(name = "itemtext", nullable = true, length = 10)
    private String itemtext;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @NotNull
    @Column(name = "value", nullable = false)
    private Float value;

    @Size(max = 1)
    @NotNull
    @Column(name = "paymentaction", nullable = false, length = 1)
    private String paymentaction;

    @NotNull
    @Column(name = "timeofaction", nullable = false)
    private LocalDateTime timeofaction;


}
