package com.common.QLSV.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Students")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentEntity{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(name = "score")
    private Double score;

    @Column(name = "class")
    private String schoolClass;

    @Column(name = "major")
    private String major;
    
    @Column(name ="graduate")
    private Boolean graduate;

    @Version
    @Column(name = "version")
    private Long version;
}
