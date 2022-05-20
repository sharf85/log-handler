package com.flightright.log_handler.repo;

import com.flightright.log_handler.model.UserEntry;

import java.util.List;
import java.util.Map;

public interface IUserEntryRepo {

    void add(UserEntry userEntry);

    void addBatch(List<UserEntry> userEntries);

    Map<String, Integer> findUniqueUsersNumberGroupedBySource();
}
