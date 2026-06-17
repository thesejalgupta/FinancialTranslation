package mp.gov.ftms.controller;

import mp.gov.ftms.dto.IntegrationStatus;
import mp.gov.ftms.integration.GovernmentIntegrationPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/integrations")
public class IntegrationController {
    private final GovernmentIntegrationPort integrationPort;

    public IntegrationController(GovernmentIntegrationPort integrationPort) {
        this.integrationPort = integrationPort;
    }

    @GetMapping("/status")
    public List<IntegrationStatus> statuses() {
        return integrationPort.statuses();
    }
}

