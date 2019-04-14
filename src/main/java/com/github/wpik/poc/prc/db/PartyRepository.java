package com.github.wpik.poc.prc.db;

import com.github.wpik.poc.prc.model.Party;
import org.springframework.data.repository.CrudRepository;

public interface PartyRepository extends CrudRepository<Party, String> {
}
