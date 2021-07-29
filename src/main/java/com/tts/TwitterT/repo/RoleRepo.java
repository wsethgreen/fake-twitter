package com.tts.TwitterT.repo;


import com.tts.TwitterT.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends CrudRepository<Role, Long> {

    Role findByRole(String role);

}
