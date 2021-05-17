package com.solexgames.rest.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 5/17/2021
 */

@Data
@Document(collection = "punishments")
public class Punishment {

    private String punishmentType;

    @Id
    private UUID id;
    private UUID issuer;
    private UUID target;
    private UUID remover;

    private Date expirationDate;
    private Date issuingDate;
    private Date createdAt;

    private String issuerName;
    private String reason;
    private String identification;
    private String removalReason = null;
    private String removerName = null;

    private boolean active;
    private boolean permanent;
    private boolean removed = false;

    private long punishmentDuration;

}
