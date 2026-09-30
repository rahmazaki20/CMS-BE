package com.cms.dto;

import java.util.List;

public record ItemImportResponse(int importedCount, List<String> errors) {
}
