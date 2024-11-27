package com.hms.service;

import com.hms.entity.Room;
import com.hms.payload.RoomDto;
import com.hms.repository.RoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    // -------------------- Constructor -------------------- //

    public RoomService(RoomRepository roomRepository, ModelMapper modelMapper) {
        this.roomRepository = roomRepository;
        this.modelMapper = modelMapper;
    }

    // ---------------------- Mapping ---------------------- //

    Room mapToEntity(RoomDto appUserDto) {
        return modelMapper.map(appUserDto, Room.class);
    }
    RoomDto mapToDto(Room room) {
        return modelMapper.map(room, RoomDto.class);
    }

    // ---------------------- Create ----------------------- //

    public boolean verifyRoom(RoomDto roomDto) {
        return roomRepository.findByRoomTypes(roomDto.getRoomTypes()).isPresent();
    }

    public RoomDto createNewRoom(RoomDto roomDto) {
        return mapToDto(roomRepository.save(mapToEntity(roomDto)));
    }

    // ----------------------- Read ----------------------- //

    public List<RoomDto> getAllRoomList() {
        return roomRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

}
