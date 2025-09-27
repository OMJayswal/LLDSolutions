package com.test;

import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

interface ShowObserver{
    void update(Show show);
}

interface ShowSubject{
    void addObserver(ShowObserver showObserver);
    void notifyAll(Show show);
}

@Data
@Builder
class Cinema{
    private int cinemaId;
    private int cityId;
    private int screenRow;
    private int screenColumn;
    private int screenCount;
}

@Data
@Builder
class Show{
    private int showId;
    private int movieId;
    private Cinema cinema;
    private long startTime;
    private long endTime;
    private int screenIndex;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Show show = (Show) o;
        return showId == show.showId && movieId == show.movieId && cinema == show.cinema && startTime == show.startTime && endTime == show.endTime && screenIndex == show.screenIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showId, movieId, cinema, startTime, endTime, screenIndex);
    }
}

class CinemaManager{
    private Map<Integer,Cinema> cinemaMap = new HashMap<>();
    public void addCinema(int cinemaId,int cityId,int screenRow,int screenColumn,int screenCount){
        if(cinemaMap.containsKey(cinemaId)){
            System.out.println("Cinema is already added");
            return;
        }
        Cinema cinema = Cinema.builder().cityId(cityId).cinemaId(cinemaId).screenColumn(screenColumn).screenRow(screenRow).screenCount(screenCount).build();
        cinemaMap.put(cinemaId,cinema);
    }
    public Cinema getCinema(Integer cinemaId){
        return cinemaMap.get(cinemaId);
    }
}

class ShowManager implements ShowSubject{
    private List<ShowObserver> observerList = new ArrayList<>();
    private Map<Integer,Show> cache = new HashMap<>();

    public Show addShow(int showId,int movieId, Cinema cinema,
                        int screenIndex, long startTime, long endTime){
        Show s = Show.builder().showId(showId).movieId(movieId).cinema(cinema).screenIndex(screenIndex).startTime(startTime).endTime(endTime).build();
        cache.put(showId,s);
        notifyAll(s);
        return s;
    }
    public void addObserver(ShowObserver showObserver){
        observerList.add(showObserver);
    }
    public void notifyAll(Show show){
        for(ShowObserver showObserver:observerList){
            showObserver.update(show);
        }
    }
    public Show getShow(Integer showId){
        return cache.get(showId);
    }
}

class ShowListener implements ShowObserver{
    private Map<String,Set<Integer>> showMap = new HashMap<>();
    List<Integer> listShows(int movieId,int cinemaId){
        Set<Integer> shows = showMap.get(movieId+"_"+cinemaId);
        return new ArrayList<>(shows);
    }
    public void update(Show show){
        showMap.computeIfAbsent(show.getMovieId()+"_"+show.getCinema().getCinemaId(),a -> new HashSet<>()).add(show.getShowId());
    }
}

class CinemaListener implements  ShowObserver{
    private Map<String,Set<Integer>> cinemaMap = new HashMap<>();

    List<Integer> listCinemas(int movieId,int cityId){
        Set<Integer> shows = cinemaMap.get(movieId+"_"+cityId);
        if(shows ==null)
            return new ArrayList<>();
        return new ArrayList<>(shows);
    }
    public void update(Show show){
        cinemaMap.computeIfAbsent(show.getMovieId()+"_"+show.getCinema().getCityId(),a -> new HashSet<>()).add(show.getShowId());
    }
}


@Data
@Builder
class Booking{
    private String ticketId;
    private Integer showId;
    private Integer ticketsCount;
    private boolean isCancelled;
    private List<String> seats;
}

class BookingManager{
    private Map<Integer,boolean[][]>  showSeats = new HashMap<>();
    private Map<String,Booking> cache = new HashMap<>();
    private Map<Integer, Integer> freeSeatsMap = new HashMap<>();


    public List<String> bookTicket(String ticketId,
                                   Show show, int ticketsCount){
        if(!showSeats.containsKey(show.getShowId())){
            boolean[][]seats  = new boolean[show.getCinema().getScreenRow()][show.getCinema().getScreenColumn()];
            showSeats.put(show.getShowId(),seats);
            freeSeatsMap.put(show.getShowId(),show.getCinema().getScreenRow()*show.getCinema().getScreenColumn());
        }
        Cinema cinema = show.getCinema();
        boolean[][]seats = showSeats.get(show.getShowId());
        if(freeSeatsMap.get(show.getShowId())<ticketsCount){
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        searchInRow:
        for(int i=0;i<cinema.getScreenRow();i++){
            for(int j=0;j<cinema.getScreenColumn();j++){
               result = bookContinuousSeatsInRow(seats[i],i,j,ticketsCount);
               if(!result.isEmpty())
                   break searchInRow;
            }
        }
        if(result.isEmpty()) {
            int count =0 ;
            searchForEmpty:
            for (int i = 0; i < cinema.getScreenRow(); i++) {
                for (int j = 0; j < cinema.getScreenColumn(); j++) {
                    if (!seats[i][j]) {
                        result.add(i + "-" + j);
                        seats[i][j] = true;
                        count++;
                    }
                    if(count == ticketsCount)
                        break searchForEmpty;
                }
            }
        }
        Booking booking = Booking.builder().ticketId(ticketId).showId(show.getShowId()).ticketsCount(ticketsCount).seats(result).build();
        cache.put(ticketId,booking);
        freeSeatsMap.compute(show.getShowId(), (k, freeSeat) -> freeSeat - ticketsCount);
        return result;
    }

    public boolean cancelTicket(String ticketId){
        if(!cache.containsKey(ticketId)) {
            System.out.println("Ticket not present");
            return false;
        }
        Booking booking = cache.get(ticketId);
        if(booking.isCancelled())
            return false;
        boolean[][] seats = showSeats.get(booking.getShowId());
        if(seats == null)
            return false;
        for(String ticket: booking.getSeats()){
            String[]rc = ticket.split("-");
            int row = Integer.parseInt(rc[0]);
            int col = Integer.parseInt(rc[1]);
            seats[row][col] = false;
        }
        freeSeatsMap.compute(booking.getShowId(), (k, freeSeat) -> freeSeat + booking.getSeats().size());
        return true;
    }

    private List<String> bookContinuousSeatsInRow(boolean[] row,int r,int start,int numberOfSeats){
        List<String> result = new ArrayList<>();
        if(start+numberOfSeats>row.length)
            return result;
        for(int i=start;i<start+numberOfSeats;i++) {
            if (row[i]) {
               return new ArrayList<>();
            }else{
                row[i] = true;
                result.add(r+"-"+i);
            }
        }
        return result;
    }

    public int getFreeSeatsCount(Show show){
        if(!freeSeatsMap.containsKey(show.getShowId()))
            return show.getCinema().getScreenColumn()*show.getCinema().getScreenRow();
        return freeSeatsMap.get(show.getShowId());
    }

}

class Solution {
    private BookingManager bookingManager;
    private CinemaListener cinemaListener;
    private ShowListener showListener;
    private ShowManager showManager;
    private CinemaManager cinemaManager;

    public void init(){
        this.cinemaManager = new CinemaManager();
        this.bookingManager = new BookingManager();
        this.showManager = new ShowManager();
        this.showListener = new ShowListener();
        this.cinemaListener = new CinemaListener();
        this.showManager.addObserver(cinemaListener);
        this.showManager.addObserver(showListener);
    }

    public void addCinema(int cinemaId, int cityId,
                          int screenCount, int screenRow, int screenColumn){
        this.cinemaManager.addCinema(cinemaId,cityId,screenRow,screenColumn,screenCount);
    }
    public void addShow(int showId, int movieId, int cinemaId,
                        int screenIndex, long startTime, long endTime){
        Cinema cinema = cinemaManager.getCinema(cinemaId);
        this.showManager.addShow(showId,movieId,cinema,screenIndex,startTime,endTime);
    }

    public List<String> bookTicket(String ticketId, int showId, int ticketsCount){
       Show show = showManager.getShow(showId);
       if(show==null)
           return new ArrayList<>();
       return bookingManager.bookTicket(ticketId,show,ticketsCount);
    }

    public boolean cancelTicket(String ticketId){
        return bookingManager.cancelTicket(ticketId);
    }

    public int getFreeTicketsCount(int showId){
        Show show = showManager.getShow(showId);
        if(show == null)
            return 0;
        return bookingManager.getFreeSeatsCount(show);
    }

    public List<Integer> listCinemas(int movieId,int cityId){
        return cinemaListener.listCinemas(movieId,cityId);
    }

    public List<Integer> listShow(int movieId,int cinemaId){
        return showListener.listShows(movieId,cinemaId);
    }
}

public class BookMyShow{
    public static void main(String []args){
        Solution obj = new Solution();
        obj.init();
        obj.addCinema(0, 1, 4, 5, 10);
        // Params: cinemaId=0, cityId=1, totalScreens=4, rows=5, seatsPerRow=10

        // 2. Add shows
        obj.addShow(
                1, // showId
                4, // movieId
                0, // cinemaId
                1, // screenIndex
                1710516108725L, // startTime
                1710523308725L  // endTime
        );

        obj.addShow(
                2, // showId
                11, // movieId
                0, // cinemaId
                3, // screenIndex
                1710516108725L, // startTime
                1710523308725L  // endTime
        );

        // 3. List cinemas for a given movie & city
        System.out.println("Cinemas for movie 0 in city 1: " +
                obj.listCinemas(0, 1));

        // 4. List shows
        System.out.println("Shows for movie 4 in cinema 0: " +
                obj.listShow(4, 0));

        System.out.println("Shows for movie 11 in cinema 0: " +
                obj.listShow(11, 0));

        // 5. Check free seats
        System.out.println("Free seats for show 1: " +
                obj.getFreeTicketsCount(1));

        // 6. Book tickets
        System.out.println("Booking ticket tkt-1 (4 seats): " +
                obj.bookTicket("tkt-1", 1, 4));

        System.out.println("Free seats for show 1 after booking: " +
                obj.getFreeTicketsCount(1));

        // 7. Cancel tickets
        System.out.println("Cancel ticket tkt-1: " +
                obj.cancelTicket("tkt-1"));

        System.out.println("Free seats for show 1 after cancel: " +
                obj.getFreeTicketsCount(1));

    }
}
