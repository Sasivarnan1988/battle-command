package com.adventure.battle.commands.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/***
 *
 * Redis Hash Command for doing the CRUD operation in the local redis instance
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RedisHash("battleCommands")
public class Command {

    private @Id
    UUID commandId;
    private String weaponSystem;
    private String battleShip;
    private String target;
    private Integer quantity;
    private BigDecimal rate;

}

