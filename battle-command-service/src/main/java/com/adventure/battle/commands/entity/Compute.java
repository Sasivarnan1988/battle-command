
package com.adventure.battle.commands.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Compute {

    private String category;
    private Integer quantity;
    private BigDecimal rate;


}


