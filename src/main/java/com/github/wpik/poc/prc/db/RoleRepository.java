package com.github.wpik.poc.prc.db;

import com.github.wpik.poc.prc.model.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, String> {
    Iterable<Role> findByPartyKey(String partyKey);
}
