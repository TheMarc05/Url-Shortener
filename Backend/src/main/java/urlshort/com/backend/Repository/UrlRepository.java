package urlshort.com.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import urlshort.com.backend.Entity.Url;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url,Long> {

    //Gaseste un URL dupa shortcode, returneaza Optional pt a evita NullPointerException
    Optional<Url> findByShortCode(String shortCode);

    //verifica daaca exista un URL cu short code-ul dat
    boolean existsByShortCode(String shortCode);

    //incrementeaza click count pt un URL
    @Modifying //marcheaza query-ul ca fiind de modificare
    @Query("UPDATE Url u SET u.clickCount = u.clickCount + 1 WHERE u.id = :id")
    void incrementClickCount(@Param("id") Long id);
    //incrementam in SQL si nu in Java:
    //-atomicitate: garanteaza ca incrementarea e thread-safe
    //-performanta: o singura operatie in DB
}
