package org.example.repository;

import org.example.model.Property;
import org.example.model.PropertyKey;
import org.example.model.Type;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PropertyRepository extends JpaRepository<Property, PropertyKey> {


    List<Property> getByOwnerAndKey_ServerNumber(String owner, Integer keyServerNumber);

    List<Property> findByOwner(String owner);

    @Query("select p from Property p where p.owner = :owner and p.key.type = :type and p.key.serverNumber = :server")
    List<Property> getOwnerByServerAndType(@Param("owner") String owner,
                                           @Param("server") Integer server,
                                           @Param("type") Type type);


    @Query("""
                select p
                from Property p
                where p.owner in (select pr.owner
                                from Property pr
                                where pr.key.serverNumber = :server
                                  and pr.key.type like :type
                                group by pr.owner
                                having count(*) = :quantity)
            """)
    List<Property> getByQuantity(@Param(value = "server") Integer server,
                                 @Param(value = "type") Type type,
                                 @Param(value = "quantity") Integer quantity);

    @Query("""
               select p from Property p
                        where p.key.serverNumber = :server
                        and p.key.type like :type
                        and p.key.propertyId between :prom1 and :prom2
            
                           and p.owner in (select pr.owner
                                         from Property pr
                                         where pr.key.serverNumber = :server
                                         group by pr.owner
                                         having count(*) = :quantity)
            
            """)
    List<Property> getByQuantityAndProm(@Param(value = "server") Integer server,
                                        @Param(value = "type") Type type,
                                        @Param(value = "prom1") Integer prom1,
                                        @Param(value = "prom2") Integer prom2,
                                        @Param(value = "quantity") Integer quantity);
}


