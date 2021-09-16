package com.miu.bookhub;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Doctor extends Role{

    private List<Visit> visits;
}
