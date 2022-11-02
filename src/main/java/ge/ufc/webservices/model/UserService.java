package ge.ufc.webservices.model;



import ge.ufc.webservices.*;

import java.net.SocketTimeoutException;

public interface UserService {

   User getUser(int id) throws AgentAccessDenied_Exception, UserNotFound_Exception, AgentAuthFailed_Exception, DatabaseException_Exception;

    int fillBalance(String trans_id,int user_id,double amount) throws AgentAccessDenied_Exception, InternalError_Exception, UserNotFound_Exception, DuplicateFault_Exception, AgentAuthFailed_Exception, AmountNotPositive_Exception, DatabaseException_Exception;


}
