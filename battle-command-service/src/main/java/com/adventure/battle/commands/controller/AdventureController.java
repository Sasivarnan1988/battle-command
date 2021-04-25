package com.adventure.battle.commands.controller;

import com.adventure.battle.commands.api.CommandApi;
import com.adventure.battle.commands.entity.Command;
import com.adventure.battle.commands.models.CommandRequest;
import com.adventure.battle.commands.models.CommandResponse;
import com.adventure.battle.commands.models.ExecutiveSummary;
import com.adventure.battle.commands.models.Summary;
import com.adventure.battle.commands.service.AdventureService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Command controller handles creation of Command,  created using Redis in-memory
 * (In memory cache for the sake for poc not in aws redis)
 * {@link CommandApi}
 *
 * @author Sasi
 */

@RestController
@Slf4j
public class AdventureController implements CommandApi {

    //private static final Logger LOG = LoggerFactory.getLogger(AdventureController.class);
    private final AdventureService adventureService;
    private final ModelMapper modelMapper;


    // Constructor Dependency Injection; instead of explicit autowire
    public AdventureController(AdventureService adventureService, ModelMapper modelMapper) {
        this.adventureService = adventureService;
        this.modelMapper = modelMapper;
    }


    private List<CommandResponse> listOfCommands = Collections.emptyList();
    private List<Summary> targetSummaryList = Collections.emptyList();
    private List<Summary> weaponSummaryList = Collections.emptyList();
    private Summary targetSummaryResponse = null;
    private Summary weaponSummaryResponse = null;

    //Map<Compute, List<Command>> collect = new ConcurrentHashMap<>();

    /***
     *
     * Entry point for the saveCommand request
     * @param listOfCommandRequest
     * @return ResponseEntity of List<CommandResponse>
     */

    @Override
    public ResponseEntity<List<CommandResponse>> saveCommand(@Valid List<CommandRequest> listOfCommandRequest) {
        log.info("Executing Save Command ={}", listOfCommandRequest);
        List<CommandResponse> listOfCommandResponse = new ArrayList<>();
        listOfCommandRequest
                .stream()
                .forEach((commandRequest) -> {
                    this.validateCommandRequest(commandRequest);
                    listOfCommandResponse.add(entityToResponse(adventureService.saveCommand(requestToEntity(commandRequest))));
                });
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(listOfCommandResponse);
    }

    /***
     * Command read endpoint to get the stream of command sent it
     * @return
     */
    @Override
    public ResponseEntity<List<CommandResponse>> getSummary() {
        listOfCommands = new ArrayList<>();
        final List<Command> commands = adventureService.getCommands();
            commands
                .stream()
                .forEach((command) -> {
                    listOfCommands.add(entityToResponse(command));
                });
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(listOfCommands);

    }

    /***
     * Command summary based on Target
     * @return
     */
    @Override
    public ResponseEntity<List<Summary>> getTargetSummary() {
       this.targetSummaryList = new ArrayList<>();
        adventureService.targetCollect.forEach((key, value) -> {
            targetSummaryResponse = new Summary();
            targetSummaryResponse.setName(key.getCategory());
            targetSummaryResponse.setAverageRate(key.getRate());
            targetSummaryResponse.setTotalQuantity(key.getQuantity());
            targetSummaryResponse.setTotalCommands(value.size());
            targetSummaryList.add(targetSummaryResponse);
        });

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(targetSummaryList);
    }


    /***
     * Command summary based on WeaponSystem
     * @return
     */
    @Override
    public ResponseEntity<List<Summary>> getWeaponSummary() {
        weaponSummaryList = new ArrayList<>();
        adventureService.weaponCollect.forEach((key, value) -> {
            weaponSummaryResponse = new Summary();
            weaponSummaryResponse.setName(key.getCategory());
            weaponSummaryResponse.setAverageRate(key.getRate());
            weaponSummaryResponse.setTotalQuantity(key.getQuantity());
            weaponSummaryResponse.setTotalCommands(value.size());
            weaponSummaryList.add(weaponSummaryResponse);
        });

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(weaponSummaryList);
    }


    /***
     * Executive summary with the combined attacks details
     * @return
     */
    @Override
    public ResponseEntity<ExecutiveSummary> getExecutiveSummary() {
        return ResponseEntity.ok(this.adventureService.getExecutiveSummary());
    }


    public Command requestToEntity(CommandRequest commandRequest) {
        Command commandDto = modelMapper.map(commandRequest, Command.class);
        commandDto.setBattleShip(commandRequest.getBattleShip());
        commandDto.setWeaponSystem(commandRequest.getWeaponSystem());
        commandDto.setTarget(commandRequest.getTarget());
        commandDto.setQuantity(commandRequest.getQuantity());
        commandDto.setRate(commandRequest.getRate());
        return commandDto;
    }

    public CommandResponse entityToResponse(Command commandDto) {
        CommandResponse commandResponse = modelMapper.map(commandDto, CommandResponse.class);
        commandResponse.setBattleShip(commandDto.getBattleShip());
        commandResponse.setWeaponSystem(commandDto.getWeaponSystem());
        commandResponse.setTarget(commandDto.getTarget());
        commandResponse.setQuantity(commandDto.getQuantity());
        commandResponse.setRate(commandDto.getRate());
        return commandResponse;
    }

    private void validateCommandRequest(CommandRequest commandRequest) {
        if (StringUtils.isEmpty(commandRequest.getBattleShip())
                || StringUtils.isEmpty(commandRequest.getTarget()) ||
                        StringUtils.isEmpty(commandRequest.getWeaponSystem()) ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "provide correct field");
        }
    }


}

