package com.api.parkingcontrol.dto;

import com.api.parkingcontrol.models.ParkingSpot;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class ParkingSpotDto  extends ParkingSpot {

    @NotBlank
    private String parkingSpotNumber;

    @NotBlank
    @Size(max = 7, min = 7)
    private String licensePlateCar;

    @NotBlank
    private String brandCar;

    @NotBlank
    private String modelCar;

    @NotBlank
    private String colorCar;

    private LocalDateTime registrationDate;

    @NotBlank
    private String responsibleName;

    @NotBlank
    private String apartment;

    @NotBlank
    private String block;

}
