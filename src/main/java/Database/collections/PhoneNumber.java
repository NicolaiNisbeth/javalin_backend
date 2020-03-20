package Database.collections;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.util.List;

@Embeddable
public class PhoneNumber {

    private String number;

    public PhoneNumber() { }
    public PhoneNumber(String number){
        this.number = number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
