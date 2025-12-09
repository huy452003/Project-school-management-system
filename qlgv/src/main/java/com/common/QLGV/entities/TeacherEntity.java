package com.common.QLGV.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeacherEntity {
    @Id
    @Column(name ="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(name = "class_managing")
    private String classManaging;

    @Column(name = "department")
    private String department;

    @Version
    @Column(name = "version")
    private Long version;
}
