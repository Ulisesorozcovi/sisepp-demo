package dev.demo.sisepp.bulk.application.port.input;

import dev.demo.sisepp.bulk.application.dto.BulkLoadSummary;
import dev.demo.sisepp.bulk.application.dto.BulkPacRequest;

public interface BulkLoadPacUseCase {
    BulkLoadSummary execute(BulkPacRequest request);
}
