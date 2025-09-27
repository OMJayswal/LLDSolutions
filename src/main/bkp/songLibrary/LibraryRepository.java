public interface LibraryRepository {
    public Library getLibrary(String libraryId);
    public Library addLibrary(Library library);
}
