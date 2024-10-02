package com.duantn.be_project.model.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalMoneyDTO {
    long amount;
    String nameOrderInfor;
    String ids;
    String address;
}
