package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vizicard.dto.DeviceDTO;
import vizicard.dto.ShortnameDTO;
import vizicard.dto.ShortnameResponse;
import vizicard.model.Profile;
import vizicard.model.Shortname;
import vizicard.model.ShortnameType;
import vizicard.repository.ShortnameRepository;
import vizicard.service.ShortnameService;
import vizicard.utils.ProfileProvider;

import java.io.IOException;

@RestController
@RequestMapping("/shortnames")
@RequiredArgsConstructor
public class ShortnameController {

    private final ShortnameService shortnameService;
    private final ShortnameRepository shortnameRepository;
    private final ProfileProvider profileProvider;
    private final ModelMapper modelMapper;

    @GetMapping
    public DeviceDTO findByShortname(String shortname) throws IOException {
        return shortnameService.findByShortname(shortname);
    }

    @PostMapping("my")
    @PreAuthorize("isAuthenticated()")
    public ShortnameResponse create(@RequestBody ShortnameDTO dto) {
        return modelMapper.map(shortnameService.create(dto.getShortname(), dto.getType()), ShortnameResponse.class);
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

    private Profile getReferrer(Integer invitorId) {
        if (invitorId != null) {
            return profileProvider.getTarget(invitorId);
        } else {
            return profileProvider.getUserFromAuth();
        }
    }

}
