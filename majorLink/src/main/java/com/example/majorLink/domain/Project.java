package com.example.majorLink.domain;

import com.example.majorLink.domain.enums.CheckStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Project extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String projectName;
    @Column(nullable = false, length = 50)
    private String space;
    private String start;
    private String end;
    @Enumerated(EnumType.STRING)
    private CheckStatus checkStatus = CheckStatus.UNCHECK;
    @Column(nullable = false, length = 255)
    private String projectDescript;

}