package Database.collections;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class User {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String authorId;

    private String authorName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Set<Playground> playgroundAdmin = new HashSet<>();

    // constructors, getters and setters...

    User() {
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

/*    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }*/

    public Set<Playground> getPlaygroundAdmin() {
        return playgroundAdmin;
    }

    public void setPlaygroundAdmin(Set<Playground> playgroundAdmin) {
        this.playgroundAdmin = playgroundAdmin;
    }
}
