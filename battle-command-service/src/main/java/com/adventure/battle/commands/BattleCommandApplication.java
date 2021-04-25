package com.adventure.battle.commands;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BattleCommandApplication {

	public static void main(String[] args) {
		SpringApplication.run(BattleCommandApplication.class, args);
	}

}
