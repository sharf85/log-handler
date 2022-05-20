package com.flightright.log_handler.repo;

import com.flightright.log_handler.model.User;
import com.flightright.log_handler.model.UserEntry;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.toMap;

@Component
public class UserEntryRepoMock implements IUserEntryRepo {
    private final ConcurrentMap<String, Set<User>> sourceToUsers = new ConcurrentHashMap<>();

    @Override
    public void add(UserEntry userEntry) {
        sourceToUsers.compute(userEntry.getSource(), (source, userSet) -> {
            if (userSet == null)
                userSet = new HashSet<>();

            userSet.add(userEntry.getUser());
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
