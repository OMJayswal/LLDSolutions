/*
 Item
 -id
 -name
 -price
 -tax percentage

-LineItem
-id
-item_id
-Qty
-price
-tax

 Invoice
 -List<LineItem> lineItems
 -TotalTax
 -TotalAmount
 */



public class Main {

    public static void main(String[] args) throws Exception {
        SongRepository songRepository = new InmemorySongRepository();
        LibraryRepository libraryRepository = new InmemoryLibraryRepository();
        LibrarySongRepository librarySongRepository = new InmemoryLibrarySongRepository();

        LibraryManager libraryManager = new LibraryManager(songRepository,libraryRepository,librarySongRepository);
        Library library = libraryManager.createLibrary("My Favourites");
        Song song = libraryManager.createSong("ThuderStruck","ACDC");
        libraryManager.addSongToLibrary(song.getSongId(),library.getId());
        Song song2 = libraryManager.createSong("BackInBlack","ACDC");
        libraryManager.addSongToLibrary(song2.getSongId(),library.getId());
        Song song3 = libraryManager.createSong("My Girl","Nirvana");
        libraryManager.addSongToLibrary(song3.getSongId(),library.getId());
        Song song4 = libraryManager.createSong("My Face","Nirvana");
        libraryManager.addSongToLibrary(song4.getSongId(),library.getId());



        Player player = new ShufflePlayer();
        player.play(libraryManager.getLibrarySong(library.getId()));
        player.play(libraryManager.getLibrarySong(library.getId()));
        player.play(libraryManager.getLibrarySong(library.getId()));
        player = new RandomPlayer();
        player.play(libraryManager.getLibrarySong(library.getId()));
        player.play(libraryManager.getLibrarySong(library.getId()));
        player.play(libraryManager.getLibrarySong(library.getId()));

    }
}
