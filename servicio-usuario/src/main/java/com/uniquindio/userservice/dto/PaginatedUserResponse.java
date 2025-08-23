package com.uniquindio.userservice.dto;

import java.util.List;

public record PaginatedUserResponse(
        int totalItems,
        int totalPages,
        int currentPage,
        int pageSize,
        List<UserResponse> users
) {
}
