package com.bakirwebservice.invoiceservice.repository;

import com.bakirwebservice.invoiceservice.entity.ErrorCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorCodesRepository extends JpaRepository<ErrorCodes,String> {

}
