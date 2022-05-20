package com.flightright.log_handler.service;

import com.flightright.log_handler.model.User;
import com.flightright.log_handler.model.UserEntry;
import com.flightright.log_handler.repo.IUserEntryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEntryServiceTest {

    @Mock
    IUserEntryRepo userEntryRepo;

    UserEntryService userEntryService;

    @BeforeEach
    public void init() {
        userEntryService = spy(new UserEntryService(",", userEntryRepo));
    }

    @Test
    public void testMapLineToUserEntry_correctLine_nonEmptyOptional() {
        String line = "test@test.com,123,google.com";

        Optional<UserEntry> optionalUserEntry = userEntryService.mapLineToUserEntry(line);

        assertTrue(optionalUserEntry.isPresent());

        UserEntry userEntry = optionalUserEntry.get();

        assertEquals(new User("test@test.com", "123"), userEntry.getUser());
        assertEquals("google.com", userEntry.getSource());
    }

    @Test
    public void testMapLineToUserEntry_lessColumnsNumber_emptyOptional() {
        String line = "test@test.com,123";

        Optional<UserEntry> optionalUserEntry = userEntryService.mapLineToUserEntry(line);

        assertTrue(optionalUserEntry.isEmpty());
    }

    @Test
    public void testMapLineToUserEntry_moreColumnsNumber_emptyOptional() {
        String line = "test@test.com,123,google.com,hello";

        Optional<UserEntry> optionalUserEntry = userEntryService.mapLineToUserEntry(line);

        assertTrue(optionalUserEntry.isEmpty());
    }

    @Test
    public void testMapLineToUserEntry_emptyColumn_emptyOptional() {
        String line1 = "test@test.com,,google.com";
        String line2 = ",123,google.com";
        String line3 = "test@test.com,123,";

        Optional<UserEntry> optionalUserEntry1 = userEntryService.mapLineToUserEntry(line1);
        Optional<UserEntry> optionalUserEntry2 = userEntryService.mapLineToUserEntry(line2);
        Optional<UserEntry> optionalUserEntry3 = userEntryService.mapLineToUserEntry(line3);

        assertTrue(optionalUserEntry1.isEmpty());
        assertTrue(optionalUserEntry2.isEmpty());
        assertTrue(optionalUserEntry3.isEmpty());
    }

    @Test
    public void testHandleCsvLine_correctLine_storeToDb() {
        UserEntry userEntry = mock(UserEntry.class);
        when(userEntryService.mapLineToUserEntry("correctLine")).thenReturn(Optional.of(userEntry));

        userEntryService.handleCsvLine("correctLine");

        verify(userEntryRepo, times(1)).add(userEntry);
    }

    @Test
    public void testHandleCsvLine_incorrectLine_notStoreToDb() {
        when(userEntryService.mapLineToUserEntry("correctLine")).thenReturn(Optional.empty());

        userEntryService.handleCsvLine("correctLine");

        verify(userEntryRepo, times(0)).add(any());
    }

    @Test
    public void testGetUniqueUsersNumberGroupedBySource() {
        Map<String, Integer> sourceToUniqUsersNumber = mock(Map.class);
        when(userEntryRepo.findUniqueUsersNumberGroupedBySource()).thenReturn(sourceToUniqUsersNumber);

        assertEquals(sourceToUniqUsersNumber, userEntryService.getUniqueUsersNumberGroupedBySource());
    }

    @Test
    public void testHandleCsvLinesBatch_nonEmpty_List() {
        List<String> lines = List.of(
                "test@test.com,123,google.com",
                "test@test.com,123,linkedin.com",
                "test@test.com,,google.com",
                "test@test.com,123,",
                "test@test.com,321,google.com",
                "test@test.com,239,google.com",
                "test@test.com,321,linkedin.com"
        );

        userEntryService.handleCsvLinesBatch(lines);

        verify(userEntryRepo, times(1)).addBatch(argThat(
                argument -> {
                    List<UserEntry> expected = List.of(
                            new UserEntry("test@test.com", "123", "google.com"),
                            new UserEntry("test@test.com", "123", "linkedin.com"),
                            new UserEntry("test@test.com", "321", "google.com"),
                            new UserEntry("test@test.com", "239", "google.com"),
                            new UserEntry("test@test.com", "321", "linkedin.com")
                    );
                    return expected.equals(argument);
                }
        ));
    }
}
