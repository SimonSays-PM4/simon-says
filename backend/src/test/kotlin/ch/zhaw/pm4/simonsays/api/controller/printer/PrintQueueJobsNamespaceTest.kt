package ch.zhaw.pm4.simonsays.api.controller.printer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class PrintQueueJobsNamespaceTest {
    @ParameterizedTest
    @CsvSource(
        "/socket-api/v1/printer-servers/wxyz/print-queues/abcd/jobs, wxyz, abcd, ", // Both IDs without job id
        "/socket-api/v1/printer-servers/1234/print-queues/5678/jobs/9012, 1234, 5678, 9012", // Numeric ids including job id
        "/socket-api/v1/printer-servers/abc123/print-queues/def456/jobs/ghi789, abc123, def456, ghi789", // Alphanumeric ids
        "/socket-api/v1/printer-servers/1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6/print-queues/7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t/jobs/a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4, 1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6, 7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t, a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4", // UUIDs for all ids
        "/socket-api/v1/printer-servers/1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6/print-queues/7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t/jobs/next, 1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6, 7g8h9i0j-1k2l-3m4n-5o6p-7q8r9s0t, next" // Match next
    )
    fun `test print queue jobs namespace pattern matching with valid inputs`(
        input: String, expectedPrinterServerId: String?, expectedPrintQueueId: String?, expectedJobId: String?
    ) {
        val matchResult = PrintQueueJobsNamespace.namespacePattern.matchEntire(input)
        assertNotNull(matchResult, "Expected a match for input: $input")
        assertEquals(expectedPrinterServerId, matchResult!!.groups[1]?.value)
        assertEquals(expectedPrintQueueId, matchResult.groups[2]?.value)
        assertEquals(expectedJobId, matchResult.groups[3]?.value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/socket-api/v1/printer-servers/wxyz/print-queues/", // Missing print queue id and job id
            "/socket-api/v1/printer-servers/wxyz/jobs/123", // Incorrect path, missing print queue id
            "/invalid-api/v1/printer-servers/1234/print-queues/5678/jobs", // Invalid base path
            "/socket-api/v1/printer-servers/1234/print-queues//jobs/789", // Missing print queue id
            "/socket-api/v1/printer-servers//print-queues/abcd/jobs/efgh" // Missing printer server id
        ]
    )
    fun `test print queue jobs namespace pattern matching with invalid inputs`(input: String) {
        val matchResult = PrintQueueJobsNamespace.namespacePattern.matchEntire(input)
        assertNull(matchResult, "Expected no match for input: $input")
    }


}