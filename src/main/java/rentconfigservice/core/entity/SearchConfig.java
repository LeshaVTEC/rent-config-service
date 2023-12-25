package rentconfigservice.core.entity;

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
