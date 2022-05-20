package com.flightright.log_handler.repo;

import com.flightright.log_handler.model.UserEntry;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Repository
public class UserEntryRepo implements IUserEntryRepo {
    private final EntityManager em;

    public UserEntryRepo(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void add(UserEntry userEntry) {
        em.persist(userEntry);
    }

    @Override
    @Transactional
    public void addBatch(List<UserEntry> userEntries) {
        userEntries.forEach(em::persist);
    }

    @Override
    public Map<String, Integer> findUniqueUsersNumberGroupedBySource() {
        return em.createQuery(
                "SELECT ue.source as source, COUNT (DISTINCT ue.user) as countUsers FROM UserEntry ue GROUP BY ue.source", Tuple.class)
                .getResultList()
                .stream()
                .collect(toMap(
                        tuple -> (String) tuple.get("source"),
                        tuple -> ((Long) tuple.get("countUsers")).intValue())
                );
    }
}
