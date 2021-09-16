package com.miu.bookhub;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Visit {

    private Doctor doctor;
    private Patient patient;
    private List<Drag> drags;
    private Integer ratting;
    private List<Diagnosis> diagnoses;
    private LocalDateTime date;
}
