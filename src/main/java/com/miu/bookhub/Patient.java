package com.miu.bookhub;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Patient extends Role{

    private List<Doctor> doctors;
    private List<Visit> visits;
}
