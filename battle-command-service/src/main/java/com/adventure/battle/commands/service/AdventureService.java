
package com.adventure.battle.commands.service;

import com.adventure.battle.commands.component.CommandHelper;
import com.adventure.battle.commands.entity.Command;
import com.adventure.battle.commands.entity.Compute;
import com.adventure.battle.commands.models.CombinedAttack;
import com.adventure.battle.commands.models.CombinedAttackInfo;
import com.adventure.battle.commands.models.ExecutiveSummary;
import com.adventure.battle.commands.repository.AdventureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
public class AdventureService {

    public Map<Compute, List<Command>> targetCollect = new ConcurrentHashMap<>();
    public Map<Compute, List<Command>> weaponCollect = new ConcurrentHashMap<>();
    public Map<String, String> executiveCollect = new ConcurrentHashMap<>();


    private final AdventureRepository adventureRepository;
    private final CommandHelper commandHelper;

    // Constructor Dependency Injection; instead of explicit autowire
    public AdventureService(AdventureRepository adventureRepository, CommandHelper commandHelper) {
        this.adventureRepository = adventureRepository;
        this.commandHelper = commandHelper;
    }

    /***
     * Persisting the commands in memory Redis running in local environment
     * @param command
     * @return command object
     */
    public Command saveCommand(Command command) {
        return adventureRepository.save(command);
    }

    /***
     * To Retrieve the command from in memory Redis
     * @return List of command Object
     */
    public List<Command> getCommands() {
        return (List<Command>) adventureRepository.findAll();

    }

    public ExecutiveSummary getExecutiveSummary() {
        final List<Command> commandList = getCommands();
        return this.calculateCombinedAttacks(commandList);
    }

    /**
     * Calculate total quantity
     *
     * @param summary
     * @param commandList
     */
    private void calculateTotalQuantity(ExecutiveSummary summary, List<Command> commandList) {
        Long totalQuantity = commandList.stream().mapToLong(dto -> dto.getQuantity()).sum();
        summary.setTotalQuantity(totalQuantity);
    }

    /**
     * average Rate
     *
     * @param summary
     * @param commandList
     */
    private void calculateTotalPrice(ExecutiveSummary summary, List<Command> commandList) {
        BigDecimal totalprice = commandList.stream().map(command -> command.getRate()).reduce(BigDecimal.ZERO, BigDecimal::add);
        Long count = Long.valueOf(commandList.size());
        summary.setAverageRate(totalprice.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP));
        summary.setTotalCommands(count);
    }

    /**
     *
     * Asynchronous logic on the commands based on weapon, target and executive systems
     * This method gets executed every one minute
     */

    public void performAsyncAnalysis() {

        targetCollect = getCommands()
                .stream()
                .collect(groupingBy(Command::getTarget))
                .entrySet()
                .stream()
                .collect(toMap(x -> {
                    Integer sumAmount = x.getValue().stream().mapToInt(Command::getQuantity).sum();
                    Double sumPrice = x.getValue().stream().mapToDouble(command -> command.getRate().doubleValue()).average().getAsDouble();
                    return new Compute(x.getKey(), sumAmount, BigDecimal.valueOf(sumPrice));
                }, Map.Entry::getValue));

        weaponCollect = getCommands()
                .stream()
                .collect(groupingBy(Command::getWeaponSystem))
                .entrySet()
                .stream()
                .collect(toMap(x -> {
                    Integer sumAmount = x.getValue().stream().mapToInt(Command::getQuantity).sum();
                    Double sumPrice = x.getValue().stream().mapToDouble(command -> command.getRate().doubleValue()).average().getAsDouble();
                    return new Compute(x.getKey(), sumAmount, BigDecimal.valueOf(sumPrice));
                }, Map.Entry::getValue));

    }

    private ExecutiveSummary calculateCombinedAttacks(List<Command> commandList) {
        List<CombinedAttackInfo> listOfCombinedAttacks = new ArrayList<>();
        ExecutiveSummary summary = new ExecutiveSummary();
        Map<String, Long> combinedAttack = commandList.stream().collect(groupingBy(command -> commandHelper.getFormattedCommandString(command), Collectors.counting()));
        calculateTotalQuantity(summary, commandList);
        calculateTotalPrice(summary, commandList);
        combinedAttack.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .collect(Collectors.toSet())
                .forEach((entry) -> {
                    String[] split = entry.getKey().toString().split("-");
                    log.info(split[0]+"::"+split[1]+""+split[2]);
                    int sumAmount = 0;
                    BigDecimal sumRate = BigDecimal.ZERO;
                    for(Command command : commandList) {
                        //do something with s
                        if(command.getBattleShip().equalsIgnoreCase(split[0]) && command.getWeaponSystem().equalsIgnoreCase(split[1]) && command.getTarget().equalsIgnoreCase(split[2])){
                            sumAmount = sumAmount + command.getQuantity();
                            sumRate = sumRate.add(command.getRate());
                        }
                    }
                    log.info(split[0]+"::"+split[1]+""+split[2]);
                    CombinedAttackInfo CombinedAttacks = new CombinedAttackInfo();
                    CombinedAttacks.setInfo(entry.getKey().toString());
                    CombinedAttacks.setTotalQuantity(sumAmount);
                    CombinedAttacks.setAverageRate(sumRate.divide(BigDecimal.valueOf(entry.getValue()), RoundingMode.HALF_UP));
                    listOfCombinedAttacks.add(CombinedAttacks);
                });
        CombinedAttack combinedAttackCommand = new CombinedAttack();
        combinedAttackCommand.setCount(listOfCombinedAttacks.size());
        combinedAttackCommand.setAdventure(listOfCombinedAttacks);
        summary.setCombinedAttack(combinedAttackCommand);
        return summary;
    }

}

