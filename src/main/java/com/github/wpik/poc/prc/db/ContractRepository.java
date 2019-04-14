package com.github.wpik.poc.prc.db;

import com.github.wpik.poc.prc.model.Contract;
import org.springframework.data.repository.CrudRepository;

public interface ContractRepository extends CrudRepository<Contract, String> {
}
