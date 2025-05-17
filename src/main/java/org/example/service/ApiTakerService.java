package org.example.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Property;
import org.example.model.PropertyKey;
import org.example.model.Type;
import org.example.model.util.RootData;
import org.example.repository.PropertyRepository;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiTakerService {

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final PropertyRepository propertyRepository;


    @Scheduled(fixedRate = 1_800_000)
    public void fetchPeriod() {
        log.info("Начался опрос api {}", LocalTime.now());
        LocalTime startAll = LocalTime.now();
        for (int i = 1; i < 32; i++) {
            LocalTime start = LocalTime.now();
            log.info("Начался опрос {} сервера в {}", i, start);
            fetchAndSaveById(i);
            LocalTime end = LocalTime.now();
            log.info("Закончил опрос {} сервер в {}", i, end);
        }
        LocalTime endAll = LocalTime.now();
        log.warn("Время выполнения {}", Duration.between(startAll, endAll));
        log.info("Опрос api успешно завершен {}", LocalTime.now());
    }

    public void fetchAndSaveById(Integer id) {
        try {
            String url = "https://n-api.arizona-rp.com/api/map/" + id;
            RootData data = restTemplate.getForObject(url, RootData.class);
            List<Property> properties = new ArrayList<>();

            if (data.getHouses() != null) {
                data.getHouses().getOnAuction().forEach(house -> {
                    house.setIsAuction(true);
                    house.setKey(new PropertyKey(house.getPropertyId() - 1, id, Type.House));
                });
                properties.addAll(data.getHouses().getOnAuction());
                data.getHouses().getHasOwner().forEach(house -> {
                    house.setIsAuction(false);
                    house.setKey(new PropertyKey(house.getPropertyId() - 1, id, Type.House));

                });
                properties.addAll(data.getHouses().getHasOwner());
                data.getHouses().getNoOwner().forEach(house -> {
                    house.setIsAuction(false);
                    house.setKey(new PropertyKey(house.getPropertyId() - 1, id, Type.House));

                });
                properties.addAll(data.getHouses().getNoOwner());
            }
            if (data.getBusinesses() != null) {
                data.getBusinesses().getOnAuction().forEach(biz -> {
                    biz.setIsAuction(true);
                    biz.setKey(new PropertyKey(biz.getPropertyId() - 1, id, Type.Business));
                });
                properties.addAll(data.getBusinesses().getOnAuction());
                Map<String, List<Property>> noAuction = data.getBusinesses().getNoAuction();
                for (List<Property> value : noAuction.values()) {
                    value.forEach(biz -> {
                        biz.setIsAuction(false);
                        biz.setKey(new PropertyKey(biz.getPropertyId() - 1, id, Type.Business));
                    });
                    properties.addAll(value);
                }
            }
            propertyRepository.saveAll(properties);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());

        }
    }
}
