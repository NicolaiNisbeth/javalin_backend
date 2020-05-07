package database.dto;

import java.util.Date;

public class DetailsDTO {

  private Date date;
  private Date startTime;
  private Date endTime;

  public DetailsDTO() {
  }

  public DetailsDTO(Date date, Date startTime, Date endTime) {
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

}
