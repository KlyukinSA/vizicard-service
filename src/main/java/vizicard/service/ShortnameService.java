package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.dto.DeviceDTO;
import vizicard.model.Shortname;
import vizicard.model.ShortnameType;
import vizicard.repository.ShortnameRepository;
import vizicard.utils.ProfileProvider;

@Service
@RequiredArgsConstructor
public class ShortnameService {

    private final ShortnameRepository shortnameRepository;
    private final ProfileProvider profileProvider;

    public DeviceDTO findByShortname(String shortname1) {
        Shortname shortname = shortnameRepository.findByShortname(shortname1);
        return new DeviceDTO(shortname.getId(), shortname.getOwner().getId(), shortname.getShortname());
    }

    public boolean add(String shortname1) {
        Shortname shortname = shortnameRepository.findByShortname(shortname1);
        if (shortname == null) {
            shortnameRepository.save(new Shortname(profileProvider.getUserFromAuth(), shortname1, ShortnameType.MAIN));
            return true;
        }
        return false;
    }

}
