package co.edu.uniquindio.laos.repositories;

import co.edu.uniquindio.laos.model.Faq;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepo extends MongoRepository<Faq, String> {

}
