package az.insta.business.media.management.api.repository;

import az.insta.business.media.management.api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {

}
