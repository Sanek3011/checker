package org.example.service;


import lombok.RequiredArgsConstructor;
import org.example.model.Property;
import org.example.model.Type;
import org.example.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;


    public List<Property> findPropertyByServerAndIntervalAndType(Integer server, Type type, Integer inter1, Integer inter2){
        return propertyRepository.findByServerNumberAndIntervalAndType(server, type, inter1, inter2);
    }


    public List<Property> getAllPropertiesByQuantityAndPromDouble(Integer server,
                                                                  Integer quantityH,
                                                                  Integer promH1,
                                                                  Integer promH2,
                                                                  Integer qunatityB,
                                                                  Integer promB1,
                                                                  Integer promB2){
        return propertyRepository.findByQuantityAndPromDouble(server, quantityH, promH1, promH2, qunatityB, promB1, promB2);
    }

    public List<Property> getAllPropertiesByNickAndServer(String nickName, Integer serverNumber) {
        if (serverNumber == 0) {
            return propertyRepository.findByOwner(nickName);
        }
        return propertyRepository.findByOwnerAndKey_ServerNumber(nickName, serverNumber);
    }

    public List<Property> getPropertiesListByTypeServerPromAndCount(Integer server,
                                                                    Type type,
                                                                    Integer prom1,
                                                                    Integer prom2,
                                                                    Integer quantity){
        return propertyRepository.findByQuantityAndProm(server, type, prom1, prom2, quantity);
    }

    public List<Property> getPropertiesByQuantityAndType(Integer server, Type type, Integer quantity ) {
        return propertyRepository.findByQuantity(server, type, quantity);
    }

    public List<Property> getPropertiesOnAuction(Integer server) {
        return propertyRepository.findByServerNumberAndIsAuction(server);
    }




}
