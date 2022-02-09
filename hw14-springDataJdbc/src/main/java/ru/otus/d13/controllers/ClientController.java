package ru.otus.d13.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.d13.models.Client;
import ru.otus.d13.models.PlainClient;
import ru.otus.d13.services.ClientService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ClientController {

    ClientService clientService;

    @GetMapping({"/"})
    public String clientsListView(Model model) {
        return "index";
    }

    @GetMapping({"/clients"})
    public String clientListView(Model model) {
        List<PlainClient> clients = clientService.findAll().stream().map(PlainClient::newInstance).collect(Collectors.toList());
        model.addAttribute("clients", clients);
        return "clients";
    }

    @GetMapping("/client")
    public String clientCreateView(Model model) {
        model.addAttribute("client", PlainClient.newInstance(new Client(null,null,null,null)));
        return "client";
    }

    @PostMapping("/client")
    public RedirectView clientSave(@ModelAttribute PlainClient client) {
        clientService.saveClient(client.getPureClient());
        return new RedirectView("/clients", true);
    }

}
