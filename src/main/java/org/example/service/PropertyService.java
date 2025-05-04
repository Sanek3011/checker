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


    public List<Property> getAllPropertiesByNickAndServer(String nickName, Integer serverNumber) {
        if (serverNumber == 0) {
            return propertyRepository.findByOwner(nickName);
        }
        return propertyRepository.getByOwnerAndKey_ServerNumber(nickName, serverNumber);
    }

    public List<Property> getPropertiesListByTypeServerPromAndCount(Integer server,
                                                                    Type type,
                                                                    Integer prom1,
                                                                    Integer prom2,
                                                                    Integer quantity){
        return propertyRepository.getByQuantityAndProm(server, type, prom1, prom2, quantity);
    }

    public List<Property> getPropertiesByQuantityAndType(Integer server, Type type, Integer quantity ) {
        return propertyRepository.getByQuantity(server, type, quantity);
    }




}
