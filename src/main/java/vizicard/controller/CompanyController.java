package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.profile.request.CompanyRequest;
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.dto.profile.response.CardResponse;
import vizicard.dto.profile.request.ProfileUpdateDTO;
import vizicard.dto.profile.request.WorkerCreateDTO;
import vizicard.dto.profile.response.CompanyResponse;
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

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public CompanyResponse createOrUpdate(@RequestBody CompanyRequest dto) {
        Card company = new Card();
        company.setName(dto.getName());
        company = companyService.prepareToCreateOrUpdate(company);
        profileService.updateProfile(company, modelMapper.map(dto, ProfileUpdateDTO.class));
        return cardMapper.mapToCompanyResponse(company);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public CompanyResponse getOfCurrentCard() {
        Card company = companyService.getOfCurrentCard();
        return cardMapper.mapToCompanyResponse(company);
    }

}
