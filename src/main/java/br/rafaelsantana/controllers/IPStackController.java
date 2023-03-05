package br.rafaelsantana.controllers;

import br.rafaelsantana.Constants;
import br.rafaelsantana.kafka.producers.IPStackProducer;
import br.rafaelsantana.model.IPStack;
import br.rafaelsantana.services.IPStackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/ipstack")
public class IPStackController {

    private final IPStackService ipStackService;

    private final IPStackProducer ipStackProducer;

    private final Constants constants;

    @Autowired
    public IPStackController(
            IPStackService ipStackService,
            IPStackProducer ipStackProducer,
            Constants constants
    ) {
        this.ipStackService = ipStackService;
        this.ipStackProducer = ipStackProducer;
        this.constants = constants;
    }

    @GetMapping("/get/{ip}")
    public IPStack getMostRecentCompleteStack(@PathVariable String ip) {
        return ipStackService.getMostRecentCompleteStack(ip).orElse(null);
    }

    @GetMapping("/getByClient/{clientId}")
    public IPStack getMostRecentStackByClient(@PathVariable String clientId) {
        return ipStackService.getMostRecentStackByClient(clientId).orElse(null);
    }

    @PostMapping("/send")
    public void sendIpStack(@RequestBody IPStack ipStack) {
        ipStackProducer.sendIPStack(ipStack, constants.INPUT_TOPIC);
    }
}
