package com.solexgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.solexgames.rest.model.Punishment;
import com.solexgames.rest.repository.PunishmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author GrowlyX
 * @since 5/17/2021
 */

@RestController
@SuppressWarnings("all")
@RequestMapping("/api/{key}/punishments")
public class PunishmentController {

    @Autowired
    private PunishmentRepository punishmentRepository;

    @RequestMapping("/id/{id}")
    public ResponseEntity<String> fetchByUuid(@PathVariable("key") String key, @PathVariable("id") String id) {
        if (!key.equals("lmao")) {
            final JsonObject object = new JsonObject();
            object.addProperty("response", "invalid-api-key");

            return new ResponseEntity<>(object.toString(), HttpStatus.OK);
        }

        final JsonArray punishmentsJson = new JsonArray();
        final Punishment punishment = this.punishmentRepository.findByIdentification(id);

        if (punishment == null) {
            final JsonObject object = new JsonObject();
            object.addProperty("response", "invalid-id");

            return new ResponseEntity<>(object.toString(), HttpStatus.OK);
        }

        final JsonObject object = new JsonObject();

        object.addProperty("uuid", punishment.getId().toString());
        object.addProperty("issuingDate", punishment.getIssuingDate().getTime());
        object.addProperty("expirationDate", punishment.getExpirationDate() == null ? 0 : punishment.getExpirationDate().getTime());
        object.addProperty("issuingName", punishment.getIssuerName());
        object.addProperty("issuingUuid", punishment.getIssuer().toString());
        object.addProperty("issuingReason", punishment.getReason());
        object.addProperty("type", punishment.getPunishmentType());
        object.addProperty("removerName", punishment.getRemoverName());
        object.addProperty("removerReason", punishment.getRemovalReason());
        object.addProperty("removerUuid", punishment.getRemover().toString());
        object.addProperty("identification", punishment.getIdentification());

        punishmentsJson.add(object);

        return new ResponseEntity<>(punishmentsJson.toString(), HttpStatus.OK);
    }
}
