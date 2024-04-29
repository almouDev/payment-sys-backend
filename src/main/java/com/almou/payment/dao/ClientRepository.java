package com.almou.payment.dao;

import com.almou.payment.beans.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client,String> {
}
