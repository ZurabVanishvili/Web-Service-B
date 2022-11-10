package ge.ufc.webservices.model;

import com.sun.xml.ws.client.BindingProviderProperties;
import ge.ufc.webservices.*;

import ge.ufc.webservices.dao.DatabaseException;
import ge.ufc.webservices.util.Utilities;
import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.MessageContext;
import jdk.jshell.execution.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;


public class UserServiceImpl implements UserService {
    private static final Logger lgg = LogManager.getLogger();

    private static UserServiceWSImpl userServiceWS;


    static {
        Agent agent = Validation.getConfiguration().getAgent();

        UserServiceWSImplService userService = new UserServiceWSImplService();
        userServiceWS = userService.getUserServiceWSImplPort();
        BindingProvider bindingProvider = (BindingProvider) userServiceWS;

        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, agent.getUrl());

        bindingProvider.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, Integer.parseInt(agent.getTimeout()));
        bindingProvider.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, Integer.parseInt(agent.getTimeout()));

        Map<String, List<String>> headers = new HashMap<>();

        headers.put("agent_id", Collections.singletonList(agent.getId()));
        headers.put("password", Collections.singletonList(agent.getPassword()));
        bindingProvider.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        lgg.trace("Headers: " + headers);

        Binding binding = bindingProvider.getBinding();
        var handlerList = binding.getHandlerChain();
        if (handlerList == null) {
            handlerList = new ArrayList<>();
        }
        handlerList.add(new SoapHandler());
        binding.setHandlerChain(handlerList);
    }


    @Override
    public User getUser(int id) throws AgentAccessDenied_Exception, UserNotFound_Exception, AgentAuthFailed_Exception, DatabaseException_Exception {
        try {
            User user = new User();
            ge.ufc.webservices.User user1 = userServiceWS.getUser(id);
            user.setBalance(user1.getBalance());
            user.setFullName(user1.getFullName());
            user.setFirstname(user1.getFirstname());
            user.setLastname(user1.getLastname());
            user.setP_number(user1.getPNumber());
            return user;
        } catch (AgentAccessDenied_Exception | UserNotFound_Exception | AgentAuthFailed_Exception | DatabaseException_Exception e) {
            throw e;
        }

    }

    @Override
    public int fillBalance(String trans_id, int user_id, double amount) throws AgentAccessDenied_Exception, UserNotFound_Exception, DuplicateFault_Exception, AgentAuthFailed_Exception, AmountNotPositive_Exception, DatabaseException_Exception, SQLException {
        try {

            return userServiceWS.pay(trans_id, user_id, amount);


        } catch (AgentAccessDenied_Exception | UserNotFound_Exception | DuplicateFault_Exception | AgentAuthFailed_Exception | AmountNotPositive_Exception | DatabaseException_Exception e) {
            lgg.error(e.getMessage());
            throw e;

        }

    }


}
