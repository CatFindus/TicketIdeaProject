import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Ticket;
import entity.TicketWrapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TicketApp {
    public static void main(String[] args) {
        System.out.println("Добро пожаловать в приложение Ticket. Укажите полный путь к файлу Ticket.json:");
        Scanner scanner = new Scanner(System.in);
        List<Ticket> tickets = getTickes(scanner)
                .stream()
                .filter(ticket -> ticket.getOriginName().equalsIgnoreCase("Владивосток") &&
                                  ticket.getDestinationName().equalsIgnoreCase("Тель-Авив"))
                .toList();
        printShortestDuration(tickets);
        printPrices(tickets);
    }

    private static void printPrices(List<Ticket> tickets) {
        List<Integer> prices = tickets.stream().map(Ticket::getPrice).sorted().toList();
        int mediumPrice = prices.stream().mapToInt(Integer::intValue).sum() / prices.size();
        int medianPrice;
        if (prices.size()%2==0) {
            medianPrice = (prices.get(prices.size()/2) + prices.get(prices.size()/2-1))/2;
        } else medianPrice = prices.get(prices.size() / 2);

        System.out.println("Разница между средней и медианной ценой: " + Math.abs(mediumPrice - medianPrice));
    }

    private static List<Ticket> getTickes(Scanner scanner) {
        ObjectMapper mapper = new ObjectMapper();
        Path path = null;
        while (true) {
            path = Path.of(scanner.nextLine());
            if (Files.exists(path) && !Files.isDirectory(path)) {
                try {
                    String jsonString = Files.readString(path);
                    jsonString = jsonString.replace("\uFEFF", "");
                    List<Ticket> tickets = mapper.readValue(jsonString, TicketWrapper.class).getTickets();
                    if (tickets.isEmpty()) throw new IllegalArgumentException("Что-то пощло не по плану");
                    else return tickets;
                } catch (Exception e) {
                    System.out.println("Не удается прочитать файл. Повторите попытку:");
                }
            } else {
                System.out.println("Указан не корректный файл. Повторите попытку:");
            }
        }
    }

    private static void printShortestDuration(List<Ticket> tickets) {
        Map<String, Duration> durationMap = new HashMap<>();
        for (Ticket ticket : tickets) {
            LocalDateTime from = LocalDateTime.of(ticket.getDepartureDate(), ticket.getDepartureTime());
            LocalDateTime to = LocalDateTime.of(ticket.getArrivalDate(), ticket.getArrivalTime());
            Duration ticketDuration = Duration.between(from, to);
            if (durationMap.containsKey(ticket.getCarrier())) {
                if (ticketDuration.compareTo(durationMap.get(ticket.getCarrier())) < 0)
                    durationMap.replace(ticket.getCarrier(), ticketDuration);
            } else {
                durationMap.put(ticket.getCarrier(), ticketDuration);
            }
        }
        for (Map.Entry<String, Duration> entry : durationMap.entrySet()) {
            System.out.println("Минимальная продолжительность перелета для: " + entry.getKey() + " составляет " + entry.getValue().toMinutes() + " минут");
        }

    }
}
