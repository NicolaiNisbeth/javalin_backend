package soap;


import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface ISoap {

  int PORT = 8084;
  String PATH = "";
  String DOMAIN = "172.31.41.170";
  String URL = String.format("http://%s:%d/%s", DOMAIN, PORT, PATH);

  @WebMethod
  String displayUserStatistics();

  @WebMethod
  String displayEventStatistics();

  @WebMethod
  String displayPlaygroundStatistics();

  @WebMethod
  String displayMessageStatistics();
}
