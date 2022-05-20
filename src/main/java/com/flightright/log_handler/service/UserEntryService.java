package com.flightright.log_handler.service;

import com.flightright.log_handler.model.UserEntry;
import com.flightright.log_handler.repo.IUserEntryRepo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserEntryService {

    private final String delimiter;
    private final IUserEntryRepo userEntryRepo;

    public UserEntryService(@Value("${com.flightright.java_spring.csv_delimiter}") String delimiter,
                            // just specify here the necessary implementation
                            @Qualifier("userEntryRepoHashMock") IUserEntryRepo userEntryRepo) {
        this.delimiter = delimiter;
        this.userEntryRepo = userEntryRepo;
    }

    public void handleCsvLine(String line) {
        Optional<UserEntry> userEntryOptional = mapLineToUserEntry(line);
        userEntryOptional.ifPresent(userEntryRepo::add);
    }

    Optional<UserEntry> mapLineToUserEntry(String line) {
        String[] parts = line.split(delimiter);

        if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank() || parts[2].isBlank())
            return Optional.empty();

        return Optional.of(new UserEntry(parts[0], parts[1], parts[2]));
    }

    public Map<String, Integer> getUniqueUsersNumberGroupedBySource() {
        return userEntryRepo.findUniqueUsersNumberGroupedBySource();
    }

    public void handleCsvLinesBatch(List<String> linesToExecute) {
        userEntryRepo.addBatch(
                linesToExecute.stream()
                        .map(this::mapLineToUserEntry)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
    }
}
