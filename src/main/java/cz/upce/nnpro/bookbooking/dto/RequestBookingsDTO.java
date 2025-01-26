package cz.upce.nnpro.bookbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestBookingsDTO {
    private List<Long> returnIds;
    private List<Long> loanIds;
}
