package org.example.repository;

import org.example.model.Property;
import org.example.model.PropertyKey;
import org.example.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PropertyRepository extends JpaRepository<Property, PropertyKey> {

    @Modifying
    @Query("update Property p set p.owner = :owner, p.name = :name, p.isAuction = :isAuction where p.key = :pkey")
    void updateProperty(@Param(value = "pkey")PropertyKey propertyKey,
                        @Param(value = "owner") String owner,
                        @Param(value = "name") String name,
                        @Param(value = "isAuction") Boolean isAuction);

    List<Property> findByOwnerAndKey_ServerNumber(String owner, Integer keyServerNumber);


    @Query("select p from Property p where p.key.serverNumber = :server and p.isAuction = true")
    List<Property> findByServerNumberAndIsAuction(@Param(value = "server") Integer serverNumber);


    List<Property> findByOwner(String owner);

    @Query("select p from Property p where p.key.serverNumber = :server and p.key.type = :type and p.key.propertyId between :inter1 and :inter2")
    List<Property> findByServerNumberAndIntervalAndType(@Param("server") Integer server,
                                                        @Param("type") Type type,
                                                        @Param("inter1") Integer inter1,
                                                        @Param("inter2") Integer inter2);

    @Query("select p from Property p where p.owner = :owner and p.key.type = :type and p.key.serverNumber = :server")
    List<Property> findOwnerByServerAndType(@Param("owner") String owner,
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
    List<Property> findByQuantity(@Param(value = "server") Integer server,
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
                                          and pr.key.type = :type
                                         group by pr.owner
                                         having count(*) = :quantity)
            
            """)
    List<Property> findByQuantityAndProm(@Param(value = "server") Integer server,
                                         @Param(value = "type") Type type,
                                         @Param(value = "prom1") Integer prom1,
                                         @Param(value = "prom2") Integer prom2,
                                         @Param(value = "quantity") Integer quantity);


    @Query("""
            select p
            from Property p
            where p.key.serverNumber = :server
              and p.owner in (
                select
                    pr.owner
                from Property pr
                where pr.key.serverNumber = :server
                group by pr.owner
                having count(*) filter (
                    where pr.key.type = 'House' and pr.key.propertyId between :promH1 and :promH2
                    ) = :quantityH
                   and count(*) filter (
                    where pr.key.type = 'Business' and pr.key.propertyId between :promB1 and :promB2
                    ) = :quantityB
            )
            """)
    List<Property> findByQuantityAndPromDouble(@Param(value = "server") Integer server,
                                               @Param(value = "quantityH") Integer quantityH,
                                               @Param(value = "promH1") Integer promH1,
                                               @Param(value = "promH2") Integer promH2,
                                               @Param(value = "quantityB") Integer quantityB,
                                               @Param(value = "promB1") Integer promB1,
                                               @Param(value = "promB2") Integer promB2);


    long countPropertiesByKey_ServerNumber(Integer keyServerNumber);
}


