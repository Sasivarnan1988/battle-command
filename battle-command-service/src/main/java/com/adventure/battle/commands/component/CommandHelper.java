package com.adventure.battle.commands.component;


import com.adventure.battle.commands.entity.Command;
import org.springframework.stereotype.Component;

@Component
public class CommandHelper {

    /**
     * Append "battleship -weaponsystem-target" to look up unique key
     *
     * @param command
     * @return
     */
    public static String getFormattedCommandString(Command command) {
        return new StringBuilder().
                append(command.getBattleShip()).
                append("-").
                append(command.getWeaponSystem()).
                append("-").
                append(command.getTarget()).toString();
    }
}

