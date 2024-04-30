package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.QRCodeResponse;
import vizicard.dto.ShortnameDTO;
import vizicard.dto.ShortnameResponse;
import vizicard.model.Card;
import vizicard.model.Shortname;
import vizicard.model.ShortnameType;
import vizicard.repository.ShortnameRepository;
import vizicard.service.CardAttributeService;
import vizicard.service.QRService;
import vizicard.service.ShortnameService;
import vizicard.utils.ProfileProvider;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shortnames")
@RequiredArgsConstructor
public class ShortnameController {

    private final ShortnameService shortnameService;
    private final ShortnameRepository shortnameRepository;
    private final ProfileProvider profileProvider;
    private final ModelMapper modelMapper;

    @GetMapping
    public ShortnameResponse findByShortname(String shortname) throws IOException {
        return modelMapper.map(shortnameRepository.findByShortname(shortname), ShortnameResponse.class);
    }

    @PutMapping("my")
    @PreAuthorize("isAuthenticated()")
    public ShortnameResponse putToCurrentCard(@RequestBody ShortnameDTO dto) {
        return modelMapper.map(shortnameService.put(dto.getShortname(), dto.getType()), ShortnameResponse.class);
    }

    @PostMapping("referral")
    @PreAuthorize("isAuthenticated()")
    public ShortnameResponse createDeviceReferralShortname(@RequestBody String sn, @RequestParam(required = false) Integer referrerId) {
        shortnameService.stopUsed(sn);
        Shortname shortname = new Shortname();
        shortname.setShortname(sn);
        shortname.setReferrer(getReferrer(referrerId));
        shortname.setType(ShortnameType.DEVICE);
        shortnameRepository.save(shortname);
        return modelMapper.map(shortname, ShortnameResponse.class);
    }

    private Card getReferrer(Integer invitorId) {
        if (invitorId != null) {
            return profileProvider.getTarget(invitorId);
        } else {
            return profileProvider.getUserFromAuth().getMainCard();
        }
    }

    @PostMapping("{id}/assign-to-main-card")
    public ShortnameResponse assignToMainCard(@PathVariable Integer id) {
        return modelMapper.map(shortnameService.assignToMainCard(id), ShortnameResponse.class);
    }

    @PostMapping("{id}/assign-to-card-or-account")
    @PreAuthorize("isAuthenticated()")
    public ShortnameResponse assignToCardByIdOrMyAccount(@PathVariable Integer id, @RequestParam(required = false) Integer cardId) {
        return modelMapper.map(shortnameService.assignToCardByIdOrMyAccount(id, cardId), ShortnameResponse.class);
    }

    @GetMapping("devices")
    public List<ShortnameResponse> getAllMyDevices() {
        return shortnameService.getAllMyDevices().stream()
                .map(s -> modelMapper.map(s, ShortnameResponse.class))
                .collect(Collectors.toList());
    }

}
