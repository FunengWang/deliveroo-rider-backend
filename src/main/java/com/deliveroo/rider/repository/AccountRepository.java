package com.deliveroo.rider.repository;

import com.deliveroo.rider.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account,Long> {
    @Override
    <S extends Account> S save(S entity);

    @Override
    void deleteById(Long aLong);

    @Override
    Optional<Account> findById(Long id);

    @Query(value = "select * from account where phone = :identity or email = :identity",nativeQuery = true)
    Optional<Account> findByIdentity(@Param("identity") String identity);

    Optional<Account> findByRiderId(String riderId);
}
