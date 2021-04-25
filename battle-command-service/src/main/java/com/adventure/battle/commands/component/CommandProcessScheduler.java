
package com.adventure.battle.commands.component;

import com.adventure.battle.commands.repository.AdventureRepository;
import com.adventure.battle.commands.service.AdventureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommandProcessScheduler {

    private final AdventureService adventureService;

    // Constructor Dependency Injection; instead of explicit autowire
    public CommandProcessScheduler(AdventureService adventureService) {
        this.adventureService = adventureService;
    }

    /***
     *
     * scheduling every minute
     */
    @Scheduled(fixedRate = 60000)
    public void scheduleTaskEveryMinute() {
        adventureService.performAsyncAnalysis();
    }

}

