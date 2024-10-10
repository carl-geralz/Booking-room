package com.enigma.challengebookingroom.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enigma.challengebookingroom.entity.Employee;
import com.enigma.challengebookingroom.entity.Equipment;
import com.enigma.challengebookingroom.entity.Reservation;
import com.enigma.challengebookingroom.entity.Room;
import com.enigma.challengebookingroom.service.impl.ReservationServiceImpl;

class ReferentialIntegrityTest {

    private static final Logger logger = LoggerFactory.getLogger(ReferentialIntegrityTest.class);

    @Mock
    private ReservationRepository reservationRepository;

        @InjectMocks
        private ReservationServiceImpl reservationService;


    private Reservation reservation;
    private Employee employee;
    private Room room;
    private Equipment equipment;

@Test
void testReferentialIntegrity() {
    when(reservationRepository.findById("res1")).thenReturn(Optional.of(reservation));

    Reservation foundReservation = reservationService.getReservationById("res1");

    assert foundReservation != null;
    assert foundReservation.getEmployee().equals(employee);
    assert foundReservation.getRoom().equals(room);

    assert foundReservation.getEquipments().contains(equipment);

    logger.info("Referential integrity maintained: Reservation contains employee {}, room {}, and equipment {}.",
            foundReservation.getEmployee().getEmployeeName(),
            foundReservation.getRoom().getRoomType(),
            foundReservation.getEquipments().get(0).getEquipmentName());

    verify(reservationRepository, times(1)).findById("res1");
}

}