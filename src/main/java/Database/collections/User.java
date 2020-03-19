package Database.collections;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class User {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String authorId;

    private String authorName;

    // todo lav many to many
    @OneToOne
    private Playground playground;
    // constructors, getters and setters...

    public User() {
    }

    public User(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String toString() {
        return "User{" +
                "authorId='" + authorId + '\'' +
                ", authorName='" + authorName + '\'' +
                "\n, playground=" + playground +
                '}';
    }

}
