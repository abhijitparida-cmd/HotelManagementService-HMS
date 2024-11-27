package com.hms.service;

import com.hms.entity.Price;
import com.hms.payload.PriceDto;
import com.hms.repository.HotelsRepository;
import com.hms.repository.PriceRepository;
import com.hms.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceService {

    private final PriceRepository priceRepository;
    private final RoomRepository roomRepository;
    private final HotelsRepository hotelsRepository;

    // --------------------- Constructor --------------------- //

    public PriceService(PriceRepository priceRepository, RoomRepository roomRepository,
                        HotelsRepository hotelsRepository) {
        this.priceRepository = priceRepository;
        this.roomRepository = roomRepository;
        this.hotelsRepository = hotelsRepository;
    }

    // ---------------------- Converts ---------------------- //

    public Price convertDtoToEntity(PriceDto priceDto) {
        Price price = new Price();
        hotelsRepository.findByHotelName(priceDto.getHotelName()).ifPresent(price::setHotelId);
        roomRepository.findByRoomTypes(priceDto.getRoomTypes()).ifPresent(price::setRoomId);
        price.setPriceOfRooms(priceDto.getPriceOfRooms());
        return price;
    }

    public PriceDto convertEntityToDto(Price price) {
        PriceDto dto = new PriceDto();
        dto.setHotelName(price.getHotelId().getHotelName());
        dto.setRoomTypes(price.getRoomId().getRoomTypes());
        dto.setPriceOfRooms(price.getPriceOfRooms());
        return dto;
    }

    // ----------------------- Create ----------------------- //

    public boolean verifyHotels(PriceDto priceDto) {
        return hotelsRepository.findByHotelName(priceDto.getHotelName()).isEmpty();
    }

    public boolean verifyPricesOfRoom(PriceDto priceDto) {
        return priceRepository.findByPriceOfRooms(priceDto.getPriceOfRooms()).isEmpty();
    }

    public PriceDto addNewPrice(PriceDto priceDto) {
        return convertEntityToDto(priceRepository.save(convertDtoToEntity(priceDto)));
    }

    // ------------------------ Read ----------------------- //

    public List<PriceDto> getAllPriceDetails() {
        return priceRepository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    // ----------------------- Finder ----------------------- //

    public boolean priceById(Long id) {
        return priceRepository.findById(id).isPresent();
    }

    public boolean verifyRoomTypesName(PriceDto priceDto) {
        return roomRepository.findByRoomTypes(priceDto.getRoomTypes()).isPresent();
    }

    // ----------------------- Update ----------------------- //

    public PriceDto updateNewPrices(Long id, PriceDto priceDto) {
        Price price = priceRepository.findById(id).get();
        price.setHotelId(hotelsRepository.findByHotelName(priceDto.getHotelName()).get());
        price.setRoomId(roomRepository.findByRoomTypes(priceDto.getRoomTypes()).get());
        price.setPriceOfRooms(priceDto.getPriceOfRooms());
        return convertEntityToDto(priceRepository.save(price));
    }

    // ----------------------- Delete ----------------------- //

    public void deletePricesById(Long id) {
        if (priceRepository.findById(id).isPresent()) {
            priceRepository.deleteById(id);
        } else {
            throw new RuntimeException("Room types with ID " + id + " does not exist.");
        }
    }
}
