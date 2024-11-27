package com.hms.service;

import com.hms.entity.Dates;
import com.hms.payload.DatesDto;
import com.hms.repository.DatesRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DatesService {

    private final DatesRepository datesRepository;
    private final ModelMapper modelMapper;

    // ----------------- Constructors ----------------- //

    public DatesService(DatesRepository datesRepository, ModelMapper modelMapper) {
        this.datesRepository = datesRepository;
        this.modelMapper = modelMapper;
    }

    // ------------------- Mapping ------------------- //

    Dates mapToEntity (DatesDto datesDto) {
        return modelMapper.map(datesDto, Dates.class);
    }

    DatesDto mapToDto (Dates dates) {
        return modelMapper.map(dates, DatesDto.class);
    }

    // -------------------- Create -------------------- //

    public List<DatesDto> addDates(int year, int month) {
        // Get the number of days in the given month and year
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Create a list to hold new dates
        List<Dates> datesList = new ArrayList<>();

        // Loop through all the days in the month
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate localDate = LocalDate.of(year, month, day);

            // Convert LocalDate to java.sql.Date
            Date sqlDate = java.sql.Date.valueOf(localDate);

            // Check if the date already exists in the database
            if (datesRepository.findByDateLists(sqlDate).isEmpty()) {
                // If the date doesn't exist, create a new Dates entity and add it to the list
                Dates dateEntity = new Dates();
                dateEntity.setDate_lists(sqlDate);
                datesList.add(dateEntity);
            }
        }

        // Save all new dates to the database
        List<Dates> savedDates = datesRepository.saveAll(datesList);

        // Map the saved Dates entities to DatesDto and return
        List<DatesDto> datesDtoList = new ArrayList<>();
        for (Dates date : savedDates) {
            datesDtoList.add(mapToDto(date)); // Use the existing mapToDto method
        }

        return datesDtoList;
    }


}
