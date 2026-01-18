package io.github.vishalmysore.ucp.server.discovery;

import com.t4a.api.AIAction;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.api.GroupInfo;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.transform.PromptTransformer;
import io.github.vishalmysore.a2a.domain.AgentCard;
import io.github.vishalmysore.a2a.server.RealTimeAgentCardController;
import io.github.vishalmysore.a2a.server.SpringAwareAgentCardController;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
@Log
@RestController
@RequestMapping(RealTimeAgentCardController.WELL_KNOWN_PATH)
public class UCPController extends SpringAwareAgentCardController {
    private PromptTransformer promptTransformer;

    @Autowired
    public UCPController(ApplicationContext context) {
        super(context);
    }

    @GetMapping(value = "ucp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentCard> getAgentCardForMyApp() {
       return null;

    }

    @PostConstruct
    public void init() {
        promptTransformer = PredictionLoader.getInstance().createOrGetPromptTransformer();
        Map<GroupInfo, String> groupActions = PredictionLoader.getInstance().getActionGroupList().getGroupActions();
        Map<String, AIAction> predictions = PredictionLoader.getInstance().getPredictions();
        StringBuilder realTimeDescription = new StringBuilder("This agent provides the following capabilities: ");

        for (Map.Entry<GroupInfo, String> entry : groupActions.entrySet()) {
            GroupInfo group = entry.getKey();
            String[] actionNames = entry.getValue().split(",");
            StringBuilder methodNames = new StringBuilder();

            for (String actionName : actionNames) {
                AIAction action = predictions.get(actionName.trim());
                if (action instanceof GenericJavaMethodAction ) {
                    Method m = ((GenericJavaMethodAction)action).getActionMethod();
                    if (isMethodAllowed(m)) {
                        methodNames.append(",");
                        methodNames.append(actionName.trim());
                    }
                }
            }
            realTimeDescription.append(group.getGroupName())
                    .append(" (")
                    .append(group.getGroupDescription())
                    .append("), with actions: ")
                    .append(methodNames)
                    .append("; ");
        }

        if (realTimeDescription.length() > 2) {
            realTimeDescription.setLength(realTimeDescription.length() - 2);
        }

        String finalDescription = realTimeDescription.toString();

        try {
            if(groupActions.isEmpty()) {
                log.warning("No actions found for the agent card");
                AgentCard card = new AgentCard();
                storeCard(card);

            } else {
                AgentCard card = (AgentCard) promptTransformer.transformIntoPojo(
                        "use this description and also populate skills in detail " + finalDescription,
                        AgentCard.class);
                storeCard(card);

            }
            String hostName = InetAddress.getLocalHost().getHostName();
          //  this.cachedAgentCard.setUrl("http://" + hostName + ":" + serverPort);
           // poplateCardFromProperties(this.cachedAgentCard);
        } catch (AIProcessingException e) {
            log.severe("The skills are not populate in the agent card as actions are more in number \n you can either try with different processor \n" +
                    " or you can populate skills individually and add to agent card , or it could be that AI key is not initialized "+e.getMessage());
        } catch (UnknownHostException e) {
            log.warning("host not knwon set the url manually card.setUrl");
        }
    }

}



