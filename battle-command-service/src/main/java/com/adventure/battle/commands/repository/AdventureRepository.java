package com.adventure.battle.commands.repository;

import com.adventure.battle.commands.entity.Command;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
   Redis implemented out of the box by adding the Springboot-starter redis dependecy

 */
@Repository
public interface AdventureRepository extends CrudRepository<Command, UUID> {

}
