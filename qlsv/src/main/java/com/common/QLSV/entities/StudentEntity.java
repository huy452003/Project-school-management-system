package com.common.QLSV.entities;

import com.common.QLSV.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Students")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentEntity {
    @Id
    @Column(name ="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name ="firstName")
    private String firstName;
    @Column(name ="lastName")
    private String lastName;
    @Column(name ="age")
    private int age;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;
    @Column(name ="birth")
    private LocalDate birth;
    @Column(name ="graduate")
    private boolean graduate;
}
