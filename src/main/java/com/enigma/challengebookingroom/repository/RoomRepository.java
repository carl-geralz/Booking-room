package com.enigma.challengebookingroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enigma.challengebookingroom.entity.Room;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
 //   List<Room> findAllByIsAvailable(Boolean isAvailable);
}