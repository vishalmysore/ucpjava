ckage io.github.vishalmysore.ucp.server.discovery;

import io.github.vishalmysore.a2a.domain.AgentCard;
import io.github.vishalmysore.a2a.server.RealTimeAgentCardController;
import io.github.vishalmysore.a2a.server.SpringAwareAgentCardController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RealTimeAgentCardController.WELL_KNOWN_PATH)
public class UCPController extends SpringAwareAgentCardController {


    @Autowired
    public UCPController(ApplicationContext context) {
        super(context);
    }

    @GetMapping(value = "ucp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentCard> getAgentCardForMyApp() {
       return null;

    }

}



