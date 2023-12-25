package rentconfigservice.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

public class SearchConfig {

    private UUID id = UUID.randomUUID();

    private User user;

    private Integer price;

    private Integer numberOfRooms;

    private Double apartmentArea;

    private Integer floor;

    private Integer yearOfConstruction;

    private Boolean availabilityOfPhotos;

}
