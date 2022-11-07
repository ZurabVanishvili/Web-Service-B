package ge.ufc.webservices.model;

public interface InsertDao {

    String fill(String pay_id, int user_id, double amount, int trans_id, int code, int status);
}
