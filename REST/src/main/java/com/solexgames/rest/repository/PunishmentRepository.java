package com.solexgames.rest.repository;

import com.solexgames.rest.model.Punishment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author GrowlyX
 * @since 5/17/2021
 */

@RepositoryRestResource(collectionResourceRel = "punishments", path = "punishments")
public interface PunishmentRepository extends MongoRepository<Punishment, String> {

    Punishment findByIdentification(@Param("identification") String identification);

}
