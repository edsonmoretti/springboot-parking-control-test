package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dto.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpot;
import com.api.parkingcontrol.services.ParkingSpotService;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto) {
        if (parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())) {
            return new ResponseEntity<>("The license plate cart is already registered", HttpStatus.CONFLICT);
        }
        if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())) {
            return new ResponseEntity<>("The parking spot number is already registered", HttpStatus.CONFLICT);
        }
        if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())) {
            return new ResponseEntity<>("The apartment and block are already registered", HttpStatus.CONFLICT);
        }
        var parkingSpot = new ParkingSpot();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpot);
        parkingSpot.setRegistrationDate(LocalDateTime.now(ZoneId.of("America/Recife")));
        var r = parkingSpotService.save(parkingSpot);
        return ResponseEntity.status(HttpStatus.CREATED).body(r);
    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpot>> getAllParkingSpots(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
                    Pageable pageable) {
        return ResponseEntity.ok(parkingSpotService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getParkingSpotById(@PathVariable("id") UUID id) {
        Optional<ParkingSpot> parkingSpot = parkingSpotService.findById(id);
        return parkingSpot.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("The parking spot was not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpotById(@PathVariable("id") UUID id) {
        Optional<ParkingSpot> parkingSpot = parkingSpotService.findById(id);
        if (parkingSpot.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The parking spot was not found");
        }
        parkingSpotService.delete(parkingSpot.get());
        return ResponseEntity.ok("The parking spot was deleted");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpotById(@PathVariable("id") UUID id, @RequestBody @Valid ParkingSpotDto parkingSpotDto) {
        Optional<ParkingSpot> parkingSpotOptional = parkingSpotService.findById(id);
        if (parkingSpotOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The parking spot was not found");
        }
        var parkingSpot = parkingSpotOptional.get();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpot);
        parkingSpot.setId(id);
        parkingSpot.setRegistrationDate(LocalDateTime.now(ZoneId.of("America/Recife")));
        parkingSpotService.save(parkingSpot);
        var returnWithData = new Object[]{"The parking spot was updated", parkingSpotOptional.get()};
        return ResponseEntity.ok(returnWithData);
    }
}
