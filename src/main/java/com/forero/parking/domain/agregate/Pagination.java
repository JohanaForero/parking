package com.forero.parking.domain.agregate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagination {
    private int page;
    private int pageSize;
    private int total;
    private int totalPages;
}
