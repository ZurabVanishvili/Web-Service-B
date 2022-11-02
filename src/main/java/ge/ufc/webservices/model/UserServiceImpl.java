package ge.ufc.webservices.model;
import com.sun.xml.ws.client.BindingProviderProperties;
import ge.ufc.webservices.*;

import ge.ufc.webservices.InternalError;
import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.MessageContext;

import java.net.SocketTimeoutException;
import java.util.*;


public class UserServiceImpl implements UserService {
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
        bindingProvider.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,headers);

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
            user.setBalance(userServiceWS.getUser(id).getBalance());
            user.setFullName(userServiceWS.getUser(id).getFullName());
            user.setFirstname(userServiceWS.getUser(id).getFirstname());
            user.setLastname(userServiceWS.getUser(id).getLastname());
            user.setP_number(userServiceWS.getUser(id).getPNumber());
            return user;
        } catch (AgentAccessDenied_Exception | UserNotFound_Exception | AgentAuthFailed_Exception | DatabaseException_Exception e) {
           throw  e;
        }

    }

    @Override
    public int fillBalance(String trans_id, int user_id, double amount) throws AgentAccessDenied_Exception, UserNotFound_Exception, DuplicateFault_Exception, AgentAuthFailed_Exception, AmountNotPositive_Exception, DatabaseException_Exception {
        try {
            return userServiceWS.pay(trans_id,user_id,amount);
        } catch (AgentAccessDenied_Exception | UserNotFound_Exception | DuplicateFault_Exception | AgentAuthFailed_Exception | AmountNotPositive_Exception | DatabaseException_Exception e) {
            throw e;

        }

    }



}
