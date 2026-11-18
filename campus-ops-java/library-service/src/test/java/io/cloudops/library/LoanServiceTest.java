package io.cloudops.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private NotificationSender notificationSender;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(catalogClient, notificationSender);
    }

    @Test
    void checkoutSucceedsAndSendsNotification() {
        when(catalogClient.findByIsbn("9780134685991"))
            .thenReturn(new BookSummary("9780134685991", "Effective Engineering", true));

        LoanReceipt receipt = loanService.checkout("978-0134685991", "stu01");

        assertEquals("9780134685991", receipt.isbn());
        assertEquals("stu01", receipt.memberId());
        assertEquals("LN-5991-STU01", receipt.confirmationCode());
        verify(catalogClient).markCheckedOut("9780134685991", "stu01");
        verify(notificationSender).sendLoanConfirmation("stu01", "9780134685991", "LN-5991-STU01");
    }

    @Test
    void checkoutRejectsUnavailableBookWithoutMutatingCatalog() {
        when(catalogClient.findByIsbn("0134685997"))
            .thenReturn(new BookSummary("0134685997", "Busy Book", false));

        assertThrows(IllegalStateException.class, () -> loanService.checkout("0134685997", "stu01"));

        verify(catalogClient, never()).markCheckedOut(anyString(), anyString());
        verifyNoInteractions(notificationSender);
    }

    @Test
    void checkoutRejectsMalformedIsbnBeforeCallingCollaborators() {
        assertThrows(InvalidIsbnException.class, () -> loanService.checkout("bad-isbn", "stu01"));
        assertThrows(InvalidIsbnException.class, () -> loanService.checkout(" ", "stu01"));
        verifyNoInteractions(catalogClient, notificationSender);
    }
}
