package com.flightright.log_handler.repo;

import com.flightright.log_handler.collection.IntOpenHashSet;
import com.flightright.log_handler.model.UserEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.toMap;

@Component
public class UserEntryRepoHashMock implements IUserEntryRepo {

    private final ConcurrentMap<String, IntOpenHashSet> sourceToUsers = new ConcurrentHashMap<>();

    @Override
    public void add(UserEntry userEntry) {
        sourceToUsers.compute(userEntry.getSource(), (source, userSet) -> {
            if (userSet == null)
                userSet = new IntOpenHashSet();

            userSet.add(userEntry.getUser().hashCode());
            return userSet;
        });
    }

    @Override
    public void addBatch(List<UserEntry> userEntries) {
        for (UserEntry userEntry : userEntries) {
            add(userEntry);
        }
    }

    @Override
    public Map<String, Integer> findUniqueUsersNumberGroupedBySource() {
        return sourceToUsers.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, o -> o.getValue().size()));
    }
}
