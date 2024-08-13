package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketWrapper {
    private List<Ticket> tickets;

    @JsonProperty("tickets")
    public List<Ticket> getTickets() {
        return tickets;
    }

    @JsonProperty("tickets")
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
