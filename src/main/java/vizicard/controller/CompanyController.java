package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.BriefCardResponse;
import vizicard.dto.CardResponse;
import vizicard.dto.profile.ProfileUpdateDTO;
import vizicard.dto.profile.WorkerCreateDTO;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.service.CompanyService;
import vizicard.service.ProfileService;
import vizicard.mapper.CardMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profiles/my-company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CardMapper cardMapper;
    private final ModelMapper modelMapper;
    private final ProfileService profileService;

    @PostMapping("workers")
    @PreAuthorize("hasAuthority('PRO')")
    CardResponse createWorker(@RequestBody WorkerCreateDTO dto) {
        Account account = new Account();
        account.setUsername(dto.getUsername());
        account.setPassword(dto.getPassword());
        Card card = new Card();
        card.setName(dto.getName());

        companyService.createWorker(account, card);

        profileService.updateProfile(card, modelMapper.map(dto, ProfileUpdateDTO.class)); // TODO password
        return cardMapper.mapToResponse(card);
    }

    @GetMapping("workers")
    List<BriefCardResponse> getAllWorkers() {
        return companyService.getAllWorkers().stream()
                .map(cardMapper::mapToBrief)
                .collect(Collectors.toList());
    }

}
