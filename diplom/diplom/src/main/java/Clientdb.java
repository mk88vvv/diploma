public class Clientdb {
    private long id;
    private String username;
    private int rank;
    private String lastvisit;

    public Clientdb(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Clientdb(long id, String username, int rank, String lastvisit) {
        this.id = id;
        this.username = username;
        this.rank = rank;
        this.lastvisit = lastvisit;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Clientdb{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public int getRank() {
        return rank;
    }

    public String getLastvisit() {
        return lastvisit;
    }
}
